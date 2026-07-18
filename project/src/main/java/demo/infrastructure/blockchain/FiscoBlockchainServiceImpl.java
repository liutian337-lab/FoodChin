package demo.infrastructure.blockchain;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import demo.config.BlockchainProperties;
import demo.exception.BlockchainException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

@Service
public class FiscoBlockchainServiceImpl implements BlockchainService {
    private final BlockchainProperties properties;

    public FiscoBlockchainServiceImpl(BlockchainProperties properties) { this.properties = properties; }

    @Override
    public String write(BlockchainAccount account, String functionName, List<?> parameters) {
        return invoke(accountAddress(account), functionName, parameters);
    }

    @Override
    public String writeEvaluationRecord(Long foodId, String evaluationHash, BigDecimal score, String level,
            BigDecimal confidence, String modelVersion, LocalDateTime evaluationTime) {
        validateEvaluationInput(foodId, evaluationHash, score, level, confidence, modelVersion, evaluationTime);
        List<?> parameters = Arrays.asList(
                foodId,
                toBytes32(evaluationHash),
                toScaledInteger(score, 2, "score"),
                level,
                toScaledInteger(confidence, 4, "confidence"),
                modelVersion,
                evaluationTime.toEpochSecond(ZoneOffset.UTC));
        String response = invokeEvaluation(accountAddress(BlockchainAccount.PRODUCER), "recordEvaluation", parameters);
        String transactionHash = findValue(JSONUtil.parseObj(response), "transactionHash", "transaction_hash", "txHash");
        if (transactionHash == null || transactionHash.trim().isEmpty()) {
            throw new BlockchainException("EvaluationRegistry response does not contain a transaction hash", null);
        }
        return transactionHash;
    }

    @Override
    public JSONArray queryAllFood() {
        return JSONUtil.parseArray(invoke(accountAddress(BlockchainAccount.PRODUCER), "getAllFood", new JSONArray()));
    }

    @Override
    public JSONArray queryFood(String traceNumber) {
        return JSONUtil.parseArray(invoke(accountAddress(BlockchainAccount.PRODUCER), "getFood", singleParameter(traceNumber)));
    }

    @Override
    public JSONArray queryTraceInfo(String traceNumber) {
        return JSONUtil.parseArray(invoke(accountAddress(BlockchainAccount.PRODUCER), "getTraceInfo", singleParameter(traceNumber)));
    }

    @Override
    public String accountAddress(BlockchainAccount account) {
        switch (account) {
            case PRODUCER: return properties.getProducerAddress();
            case DISTRIBUTOR: return properties.getDistributorAddress();
            case RETAILER: return properties.getRetailerAddress();
            default: throw new BlockchainException("unsupported blockchain account", null);
        }
    }

    private String invoke(String userAddress, String functionName, List<?> functionParameters) {
        return invoke(userAddress, functionName, functionParameters, properties.getContractName(),
                properties.getContractAddress(), properties.getContractAbi());
    }

    private String invokeEvaluation(String userAddress, String functionName, List<?> functionParameters) {
        return invoke(userAddress, functionName, functionParameters, properties.getEvaluationContractName(),
                properties.getEvaluationContractAddress(), properties.getEvaluationContractAbi());
    }

    private String invoke(String userAddress, String functionName, List<?> functionParameters, String contractName,
            String contractAddress, String contractAbi) {
        try {
            if (contractAddress == null || contractAddress.trim().isEmpty()) {
                throw new BlockchainException("blockchain contract address is not configured", null);
            }
            if (contractAbi == null || contractAbi.trim().isEmpty()) {
                throw new BlockchainException("blockchain contract ABI is not configured", null);
            }
            JSONObject request = new JSONObject();
            request.set("contractName", contractName);
            request.set("contractAddress", contractAddress);
            request.set("contractAbi", JSONUtil.parseArray(contractAbi));
            request.set("user", userAddress);
            request.set("funcName", functionName);
            request.set("funcParam", functionParameters);
            request.set("groupId", properties.getGroupId());
            request.set("useCns", properties.isUseCns());
            return HttpRequest.post(properties.getWebaseUrl()).header(Header.CONTENT_TYPE, "application/json")
                    .timeout(properties.getTimeoutMs()).body(JSONUtil.toJsonStr(request)).execute().body();
        } catch (Exception exception) {
            throw new BlockchainException("blockchain invocation failed", exception);
        }
    }

    private JSONArray singleParameter(String traceNumber) {
        JSONArray parameters = new JSONArray();
        parameters.add(Long.parseLong(traceNumber));
        return parameters;
    }

    private void validateEvaluationInput(Long foodId, String evaluationHash, BigDecimal score, String level,
            BigDecimal confidence, String modelVersion, LocalDateTime evaluationTime) {
        if (foodId == null || foodId <= 0) throw new BlockchainException("foodId must be positive", null);
        toBytes32(evaluationHash);
        if (score == null || confidence == null || level == null || level.trim().isEmpty()
                || modelVersion == null || modelVersion.trim().isEmpty() || evaluationTime == null) {
            throw new BlockchainException("evaluation record contains required empty values", null);
        }
    }

    private String toBytes32(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.startsWith("0x") || normalized.startsWith("0X")) normalized = normalized.substring(2);
        if (!normalized.matches("[0-9a-fA-F]{64}")) {
            throw new BlockchainException("evaluationHash must be a 64-character SHA-256 hexadecimal value", null);
        }
        return "0x" + normalized.toLowerCase();
    }

    private BigInteger toScaledInteger(BigDecimal value, int scale, String fieldName) {
        try {
            return value.movePointRight(scale).toBigIntegerExact();
        } catch (ArithmeticException exception) {
            throw new BlockchainException(fieldName + " exceeds the supported decimal scale", exception);
        }
    }

    private String findValue(Object value, String... keys) {
        if (value instanceof JSONObject) {
            JSONObject object = (JSONObject) value;
            for (String key : keys) {
                if (object.containsKey(key) && object.get(key) != null) return String.valueOf(object.get(key));
            }
            for (Object child : object.values()) {
                String found = findValue(child, keys);
                if (found != null) return found;
            }
        } else if (value instanceof JSONArray) {
            for (Object child : (JSONArray) value) {
                String found = findValue(child, keys);
                if (found != null) return found;
            }
        }
        return null;
    }
}
