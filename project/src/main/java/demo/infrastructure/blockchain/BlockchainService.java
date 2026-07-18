package demo.infrastructure.blockchain;

import cn.hutool.json.JSONArray;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BlockchainService {
    String write(BlockchainAccount account, String functionName, List<?> parameters);
    String writeEvaluationRecord(Long foodId, String evaluationHash, BigDecimal score, String level,
            BigDecimal confidence, String modelVersion, LocalDateTime evaluationTime);
    JSONArray queryAllFood();
    JSONArray queryFood(String traceNumber);
    JSONArray queryTraceInfo(String traceNumber);
    String accountAddress(BlockchainAccount account);
}
