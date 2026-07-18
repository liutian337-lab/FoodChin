package demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.transaction.support.TransactionTemplate;
import demo.evaluation.infrastructure.repository.BlockchainTransactionRepository;
import demo.evaluation.infrastructure.repository.EvaluationHistoryRepository;
import demo.evaluation.infrastructure.repository.FeatureSnapshotRepository;
import demo.evaluation.infrastructure.repository.FoodEvaluationRepository;
import demo.evaluation.infrastructure.repository.ModelInfoRepository;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @MockBean private FoodEvaluationRepository foodEvaluationRepository;
    @MockBean private EvaluationHistoryRepository evaluationHistoryRepository;
    @MockBean private FeatureSnapshotRepository featureSnapshotRepository;
    @MockBean private ModelInfoRepository modelInfoRepository;
    @MockBean private BlockchainTransactionRepository blockchainTransactionRepository;
    @MockBean private TransactionTemplate transactionTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void evaluationPredictMappingIsRegistered() {
        boolean registered = requestMappingHandlerMapping.getHandlerMethods().keySet().stream().anyMatch(mapping ->
                mapping.getPatternValues().contains("/evaluations/predict")
                        && mapping.getMethodsCondition().getMethods().contains(RequestMethod.POST));
        assertTrue(registered);
    }

}
