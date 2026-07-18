package demo.evaluation.application;

import demo.config.BlockchainProperties;
import demo.evaluation.infrastructure.entity.BlockchainTransactionEntity;
import demo.evaluation.infrastructure.entity.FoodEvaluationEntity;
import demo.evaluation.infrastructure.entity.ModelInfoEntity;
import demo.evaluation.infrastructure.repository.*;
import demo.evaluation.vo.EvaluationVO;
import demo.infrastructure.ai.AIClient;
import demo.infrastructure.ai.FoodFeatureRequest;
import demo.infrastructure.ai.PredictionResponse;
import demo.infrastructure.blockchain.BlockchainService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EvaluationApplicationServiceTest {
    @Test
    void createsPredictionEvaluationAnchorsHashAndRecordsMetrics() throws Exception {
        FoodEvaluationRepository evaluations = mock(FoodEvaluationRepository.class);
        EvaluationHistoryRepository histories = mock(EvaluationHistoryRepository.class);
        FeatureSnapshotRepository snapshots = mock(FeatureSnapshotRepository.class);
        ModelInfoRepository models = mock(ModelInfoRepository.class);
        BlockchainTransactionRepository transactions = mock(BlockchainTransactionRepository.class);
        BlockchainService blockchain = mock(BlockchainService.class);
        AIClient aiClient = mock(AIClient.class);
        TransactionTemplate template = mock(TransactionTemplate.class);
        BlockchainProperties properties = new BlockchainProperties();
        properties.getEvaluationContract().setAddress("0xevaluationcontract");

        when(template.execute(any())).thenAnswer(invocation -> ((TransactionCallback<?>) invocation.getArgument(0)).doInTransaction(null));
        when(models.findByModelNameAndVersion("food-quality-random-forest", "rf-test")).thenReturn(Optional.empty());
        when(models.save(any(ModelInfoEntity.class))).thenAnswer(invocation -> {
            ModelInfoEntity entity = invocation.getArgument(0); setId(entity, 10L); return entity;
        });
        when(evaluations.save(any(FoodEvaluationEntity.class))).thenAnswer(invocation -> {
            FoodEvaluationEntity entity = invocation.getArgument(0); setId(entity, 20L); return entity;
        });
        when(transactions.save(any(BlockchainTransactionEntity.class))).thenAnswer(invocation -> {
            BlockchainTransactionEntity entity = invocation.getArgument(0); setId(entity, 30L); return entity;
        });
        when(blockchain.writeEvaluationRecord(anyLong(), anyString(), any(BigDecimal.class), anyString(),
                any(BigDecimal.class), anyString(), any())).thenReturn("0xabc");
        PredictionResponse prediction = new PredictionResponse();
        prediction.setScore(new BigDecimal("91.20")); prediction.setLevel("A"); prediction.setConfidence(new BigDecimal("0.91"));
        prediction.setModelName("food-quality-random-forest"); prediction.setModelVersion("rf-test");
        prediction.setAlgorithm("RandomForestRegressor"); prediction.setAccuracy(new BigDecimal("0.88"));
        when(aiClient.predict(any(FoodFeatureRequest.class))).thenReturn(prediction);

        EvaluationApplicationService service = new EvaluationApplicationService(evaluations, histories, snapshots, models,
                transactions, blockchain, properties, template, aiClient);
        FoodFeatureRequest request = new FoodFeatureRequest();
        request.setFoodId(7L); request.setFeatures(Collections.singletonMap("temperature", new BigDecimal("4.2")));
        EvaluationVO result = service.createFromPrediction(request);

        assertEquals(20L, result.getEvaluationId());
        assertEquals("A", result.getLevel());
        assertNotNull(result.getEvaluationHash());
        assertEquals("0xabc", result.getTransactionHash());
        assertEquals("SUCCESS", result.getStatus());
        verify(aiClient).predict(request);
        verify(blockchain).writeEvaluationRecord(eq(7L), anyString(), eq(new BigDecimal("91.20")), eq("A"),
                eq(new BigDecimal("0.91")), eq("rf-test"), any());
        verify(histories, atLeastOnce()).save(argThat(history -> "PROCESS_METRICS".equals(history.getOperation())));
    }

    private static void setId(Object entity, Long value) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true); field.set(entity, value);
    }
}
