package demo.evaluation.application;

import demo.config.BlockchainProperties;
import demo.exception.BusinessException;
import demo.evaluation.domain.EvaluationHashGenerator;
import demo.infrastructure.ai.AIClient;
import demo.infrastructure.ai.FoodFeatureRequest;
import demo.infrastructure.ai.PredictionResponse;
import demo.evaluation.dto.EvaluationCreateDTO;
import demo.evaluation.dto.EvaluationPredictDTO;
import demo.evaluation.dto.FeatureSnapshotDTO;
import demo.infrastructure.blockchain.BlockchainService;
import demo.evaluation.infrastructure.entity.*;
import demo.evaluation.infrastructure.repository.*;
import demo.evaluation.vo.EvaluationHistoryVO;
import demo.evaluation.vo.EvaluationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluationApplicationService {
    private static final String EVALUATION_ANCHOR = "EVALUATION_ANCHOR";
    private final FoodEvaluationRepository foodEvaluationRepository;
    private final EvaluationHistoryRepository evaluationHistoryRepository;
    private final FeatureSnapshotRepository featureSnapshotRepository;
    private final ModelInfoRepository modelInfoRepository;
    private final BlockchainTransactionRepository blockchainTransactionRepository;
    private final BlockchainService blockchainService;
    private final BlockchainProperties blockchainProperties;
    private final TransactionTemplate transactionTemplate;
    private final AIClient aiClient;

    public EvaluationApplicationService(FoodEvaluationRepository foodEvaluationRepository,
            EvaluationHistoryRepository evaluationHistoryRepository, FeatureSnapshotRepository featureSnapshotRepository,
            ModelInfoRepository modelInfoRepository, BlockchainTransactionRepository blockchainTransactionRepository,
            BlockchainService blockchainService, BlockchainProperties blockchainProperties, TransactionTemplate transactionTemplate,
            AIClient aiClient) {
        this.foodEvaluationRepository = foodEvaluationRepository;
        this.evaluationHistoryRepository = evaluationHistoryRepository;
        this.featureSnapshotRepository = featureSnapshotRepository;
        this.modelInfoRepository = modelInfoRepository;
        this.blockchainTransactionRepository = blockchainTransactionRepository;
        this.blockchainService = blockchainService;
        this.blockchainProperties = blockchainProperties;
        this.transactionTemplate = transactionTemplate;
        this.aiClient = aiClient;
    }

    public EvaluationVO create(EvaluationCreateDTO request) { return createEvaluation(request); }

    public EvaluationVO createFromPrediction(EvaluationPredictDTO request) {
        FoodFeatureRequest featureRequest = new FoodFeatureRequest();
        featureRequest.setFoodId(request.getFoodId()); featureRequest.setFeatures(request.getFeatures());
        return createFromPrediction(featureRequest);
    }

    /** Creates an evaluation from a FastAPI prediction and persists the exact input feature snapshot. */
    public EvaluationVO createFromPrediction(FoodFeatureRequest featureRequest) {
        long totalStartedAt = System.nanoTime();
        long aiStartedAt = System.nanoTime();
        PredictionResponse prediction = aiClient.predict(featureRequest);
        long aiLatencyMs = elapsedMillis(aiStartedAt);
        Long modelId = transactionTemplate.execute(status -> synchronizeModelInfo(prediction));
        EvaluationCreateDTO request = new EvaluationCreateDTO();
        request.setFoodId(featureRequest.getFoodId()); request.setScore(prediction.getScore()); request.setLevel(prediction.getLevel());
        request.setConfidence(prediction.getConfidence()); request.setModelId(modelId); request.setModelVersion(prediction.getModelVersion());
        List<FeatureSnapshotDTO> snapshots = new ArrayList<FeatureSnapshotDTO>();
        if (featureRequest.getFeatures() != null) {
            for (java.util.Map.Entry<String, java.math.BigDecimal> feature : featureRequest.getFeatures().entrySet()) {
                FeatureSnapshotDTO snapshot = new FeatureSnapshotDTO();
                snapshot.setFeatureName(feature.getKey()); snapshot.setFeatureValue(feature.getValue().toPlainString()); snapshot.setSource("AI_INPUT");
                snapshots.add(snapshot);
            }
        }
        request.setFeatureSnapshots(snapshots);
        return createEvaluation(request, aiLatencyMs, totalStartedAt);
    }

    /** Persists a recoverable PENDING record before attempting the external blockchain operation. */
    public EvaluationVO createEvaluation(EvaluationCreateDTO request) {
        return createEvaluation(request, null, System.nanoTime());
    }

    private EvaluationVO createEvaluation(EvaluationCreateDTO request, Long aiLatencyMs, long totalStartedAt) {
        PendingEvaluation pending = transactionTemplate.execute(status -> persistPendingEvaluation(request));
        if (pending == null) throw new IllegalStateException("Unable to persist evaluation");

        BlockchainReceipt receipt;
        long blockchainStartedAt = System.nanoTime();
        try {
            receipt = anchorOnBlockchain(pending.evaluation);
        } catch (Exception exception) {
            EvaluationVO result = transactionTemplate.execute(status -> markFailed(pending, safeMessage(exception)));
            recordProcessMetrics(pending.evaluation.getId(), aiLatencyMs, elapsedMillis(blockchainStartedAt), elapsedMillis(totalStartedAt));
            return result;
        }

        try {
            EvaluationVO result = transactionTemplate.execute(status -> markSuccess(pending, receipt));
            recordProcessMetrics(pending.evaluation.getId(), aiLatencyMs, elapsedMillis(blockchainStartedAt), elapsedMillis(totalStartedAt));
            return result;
        } catch (Exception ignored) {
            // The initial PENDING transaction remains durable and can be reconciled by request_hash.
            recordProcessMetrics(pending.evaluation.getId(), aiLatencyMs, elapsedMillis(blockchainStartedAt), elapsedMillis(totalStartedAt));
            return EvaluationVO.from(pending.evaluation, pending.transaction);
        }
    }

    public List<EvaluationVO> findAll() { return toViews(foodEvaluationRepository.findAll()); }

    public List<EvaluationVO> findByFoodId(Long foodId) { return toViews(foodEvaluationRepository.findByFoodIdOrderByEvaluationTimeDesc(foodId)); }

    public EvaluationVO findLatestByFoodId(Long foodId) {
        FoodEvaluationEntity evaluation = foodEvaluationRepository.findFirstByFoodIdOrderByEvaluationTimeDesc(foodId)
                .orElseThrow(() -> new BusinessException(404, "No evaluation result found for foodId=" + foodId));
        BlockchainTransactionEntity transaction = blockchainTransactionRepository
                .findByBusinessTypeAndBusinessId(EVALUATION_ANCHOR, evaluation.getId()).orElse(null);
        return EvaluationVO.from(evaluation, transaction);
    }

    public List<EvaluationHistoryVO> findHistory(Long evaluationId) {
        List<EvaluationHistoryVO> views = new ArrayList<EvaluationHistoryVO>();
        for (EvaluationHistoryEntity history : evaluationHistoryRepository.findByEvaluationIdOrderByCreatedTimeAsc(evaluationId)) views.add(EvaluationHistoryVO.from(history));
        return views;
    }

    public EvaluationHistoryEntity saveHistory(Long evaluationId, String operation, String description) {
        EvaluationHistoryEntity history = new EvaluationHistoryEntity();
        history.setEvaluationId(evaluationId); history.setOperation(operation); history.setDescription(description);
        history.setCreatedTime(LocalDateTime.now());
        return evaluationHistoryRepository.save(history);
    }

    public ModelInfoEntity saveModelInfo(ModelInfoEntity modelInfo) {
        if (modelInfo.getCreatedTime() == null) modelInfo.setCreatedTime(LocalDateTime.now());
        return modelInfoRepository.save(modelInfo);
    }

    private Long synchronizeModelInfo(PredictionResponse prediction) {
        if (prediction.getModelName() == null || prediction.getModelVersion() == null || prediction.getAlgorithm() == null) {
            throw new IllegalStateException("AI prediction response does not include model metadata");
        }
        ModelInfoEntity model = modelInfoRepository.findByModelNameAndVersion(prediction.getModelName(), prediction.getModelVersion())
                .orElseGet(ModelInfoEntity::new);
        model.setModelName(prediction.getModelName()); model.setVersion(prediction.getModelVersion());
        model.setAlgorithm(prediction.getAlgorithm()); model.setAccuracy(prediction.getAccuracy());
        return saveModelInfo(model).getId();
    }

    public BlockchainTransactionEntity saveBlockchainTransaction(BlockchainTransactionEntity transaction) {
        LocalDateTime now = LocalDateTime.now();
        if (transaction.getCreatedTime() == null) transaction.setCreatedTime(now);
        transaction.setUpdatedTime(now);
        if (transaction.getRetryCount() == null) transaction.setRetryCount(0);
        return blockchainTransactionRepository.save(transaction);
    }

    private PendingEvaluation persistPendingEvaluation(EvaluationCreateDTO request) {
        LocalDateTime now = LocalDateTime.now();
        FoodEvaluationEntity evaluation = new FoodEvaluationEntity();
        evaluation.setFoodId(request.getFoodId()); evaluation.setScore(request.getScore()); evaluation.setLevel(request.getLevel());
        evaluation.setConfidence(request.getConfidence()); evaluation.setModelId(request.getModelId()); evaluation.setModelVersion(request.getModelVersion());
        evaluation.setEvaluationTime(request.getEvaluationTime() == null ? now : request.getEvaluationTime());
        evaluation.setEvaluationHash(EvaluationHashGenerator.generate(evaluation.getFoodId(), evaluation.getScore(), evaluation.getLevel(),
                evaluation.getConfidence(), evaluation.getModelVersion(), evaluation.getEvaluationTime()));
        evaluation.setChainStatus("PENDING"); evaluation.setCreatedTime(now);
        FoodEvaluationEntity saved = foodEvaluationRepository.save(evaluation);
        saveHistory(saved.getId(), "CREATED", "Evaluation persisted and awaiting blockchain anchoring.");
        if (request.getFeatureSnapshots() != null) {
            for (FeatureSnapshotDTO snapshot : request.getFeatureSnapshots()) saveFeatureSnapshot(saved.getId(), saved.getFoodId(), snapshot, now);
        }
        BlockchainTransactionEntity transaction = new BlockchainTransactionEntity();
        transaction.setBusinessType(EVALUATION_ANCHOR); transaction.setBusinessId(saved.getId()); transaction.setFoodId(saved.getFoodId());
        transaction.setContractAddress(blockchainProperties.getEvaluationContractAddress()); transaction.setStatus("PENDING");
        transaction.setRequestHash(saved.getEvaluationHash()); transaction.setEvaluationHash(saved.getEvaluationHash());
        transaction.setRetryCount(0); transaction.setCreatedTime(now); transaction.setUpdatedTime(now);
        return new PendingEvaluation(saved, blockchainTransactionRepository.save(transaction));
    }

    private BlockchainReceipt anchorOnBlockchain(FoodEvaluationEntity evaluation) {
        String transactionHash = blockchainService.writeEvaluationRecord(evaluation.getFoodId(), evaluation.getEvaluationHash(),
                evaluation.getScore(), evaluation.getLevel(), evaluation.getConfidence(), evaluation.getModelVersion(),
                evaluation.getEvaluationTime());
        return new BlockchainReceipt(transactionHash, null);
    }

    private EvaluationVO markSuccess(PendingEvaluation pending, BlockchainReceipt receipt) {
        pending.evaluation.setChainStatus("SUCCESS");
        FoodEvaluationEntity evaluation = foodEvaluationRepository.save(pending.evaluation);
        pending.transaction.setTransactionHash(receipt.transactionHash); pending.transaction.setBlockNumber(receipt.blockNumber);
        pending.transaction.setStatus("SUCCESS"); pending.transaction.setLastError(null);
        BlockchainTransactionEntity transaction = saveBlockchainTransaction(pending.transaction);
        saveHistory(evaluation.getId(), "BLOCKCHAIN_SUCCESS", "Evaluation hash anchored on blockchain.");
        return EvaluationVO.from(evaluation, transaction);
    }

    private EvaluationVO markFailed(PendingEvaluation pending, String reason) {
        pending.evaluation.setChainStatus("FAILED");
        FoodEvaluationEntity evaluation = foodEvaluationRepository.save(pending.evaluation);
        pending.transaction.setStatus("FAILED"); pending.transaction.setLastError(reason);
        BlockchainTransactionEntity transaction = saveBlockchainTransaction(pending.transaction);
        saveHistory(evaluation.getId(), "BLOCKCHAIN_FAILED", reason);
        return EvaluationVO.from(evaluation, transaction);
    }

    private void recordProcessMetrics(Long evaluationId, Long aiLatencyMs, long blockchainLatencyMs, long totalLatencyMs) {
        try {
            transactionTemplate.execute(status -> {
                String ai = aiLatencyMs == null ? "N/A" : String.valueOf(aiLatencyMs);
                saveHistory(evaluationId, "PROCESS_METRICS", "aiLatencyMs=" + ai + ", blockchainLatencyMs=" + blockchainLatencyMs
                        + ", totalResponseTimeMs=" + totalLatencyMs);
                return null;
            });
        } catch (Exception ignored) {
            // Observability must not change the durable evaluation or blockchain outcome.
        }
    }

    private void saveFeatureSnapshot(Long evaluationId, Long foodId, FeatureSnapshotDTO input, LocalDateTime createdTime) {
        FeatureSnapshotEntity snapshot = new FeatureSnapshotEntity();
        snapshot.setEvaluationId(evaluationId); snapshot.setFoodId(foodId); snapshot.setFeatureName(input.getFeatureName());
        snapshot.setFeatureValue(input.getFeatureValue()); snapshot.setSource(input.getSource()); snapshot.setCreatedTime(createdTime);
        featureSnapshotRepository.save(snapshot);
    }

    private List<EvaluationVO> toViews(List<FoodEvaluationEntity> evaluations) {
        List<EvaluationVO> views = new ArrayList<EvaluationVO>();
        for (FoodEvaluationEntity evaluation : evaluations) {
            BlockchainTransactionEntity transaction = blockchainTransactionRepository.findByBusinessTypeAndBusinessId(EVALUATION_ANCHOR, evaluation.getId()).orElse(null);
            views.add(EvaluationVO.from(evaluation, transaction));
        }
        return views;
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        return message.length() > 1000 ? message.substring(0, 1000) : message;
    }

    private long elapsedMillis(long startedAt) { return (System.nanoTime() - startedAt) / 1_000_000L; }

    private static class PendingEvaluation {
        private final FoodEvaluationEntity evaluation;
        private final BlockchainTransactionEntity transaction;
        private PendingEvaluation(FoodEvaluationEntity evaluation, BlockchainTransactionEntity transaction) { this.evaluation = evaluation; this.transaction = transaction; }
    }

    private static class BlockchainReceipt {
        private final String transactionHash;
        private final Long blockNumber;
        private BlockchainReceipt(String transactionHash, Long blockNumber) { this.transactionHash = transactionHash; this.blockNumber = blockNumber; }
    }
}
