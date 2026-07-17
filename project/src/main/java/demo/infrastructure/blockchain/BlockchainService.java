package demo.infrastructure.blockchain;

import cn.hutool.json.JSONArray;
import java.util.List;

public interface BlockchainService {
    String write(BlockchainAccount account, String functionName, List<?> parameters);
    JSONArray queryAllFood();
    JSONArray queryFood(String traceNumber);
    JSONArray queryTraceInfo(String traceNumber);
    String accountAddress(BlockchainAccount account);
}
