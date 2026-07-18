package demo.evaluation.vo;

import demo.evaluation.infrastructure.entity.FoodEvaluationEntity;
import demo.evaluation.infrastructure.entity.BlockchainTransactionEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EvaluationVO {
    private Long id;
    private Long foodId;
    private BigDecimal score;
    private String level;
    private BigDecimal confidence;
    private String modelVersion;
    private String chainStatus;
    private String evaluationHash;
    private String transactionHash;
    private LocalDateTime evaluationTime;
    private LocalDateTime createdTime;

    public static EvaluationVO from(FoodEvaluationEntity evaluation) {
        EvaluationVO vo = new EvaluationVO();
        vo.id = evaluation.getId(); vo.foodId = evaluation.getFoodId(); vo.score = evaluation.getScore();
        vo.level = evaluation.getLevel(); vo.confidence = evaluation.getConfidence();
        vo.modelVersion = evaluation.getModelVersion(); vo.evaluationTime = evaluation.getEvaluationTime();
        vo.chainStatus = evaluation.getChainStatus(); vo.evaluationHash = evaluation.getEvaluationHash(); vo.createdTime = evaluation.getCreatedTime();
        return vo;
    }

    public static EvaluationVO from(FoodEvaluationEntity evaluation, BlockchainTransactionEntity transaction) {
        EvaluationVO vo = from(evaluation);
        if (transaction != null) vo.transactionHash = transaction.getTransactionHash();
        return vo;
    }

    public Long getId() { return id; }
    public Long getFoodId() { return foodId; }
    public BigDecimal getScore() { return score; }
    public String getLevel() { return level; }
    public BigDecimal getConfidence() { return confidence; }
    public String getModelVersion() { return modelVersion; }
    public String getChainStatus() { return chainStatus; }
    public String getStatus() { return chainStatus; }
    public Long getEvaluationId() { return id; }
    public String getEvaluationHash() { return evaluationHash; }
    public String getTransactionHash() { return transactionHash; }
    public LocalDateTime getEvaluationTime() { return evaluationTime; }
    public LocalDateTime getCreatedTime() { return createdTime; }
}
