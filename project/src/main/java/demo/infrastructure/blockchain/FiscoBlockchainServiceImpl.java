package demo.infrastructure.blockchain;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import demo.config.BlockchainProperties;
import demo.exception.BlockchainException;
import org.springframework.stereotype.Service;
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
        try {
            JSONObject request = new JSONObject();
            request.set("contractName", properties.getContractName());
            request.set("contractAddress", properties.getContractAddress());
            request.set("contractAbi", JSONUtil.parseArray(properties.getContractAbi()));
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
}
