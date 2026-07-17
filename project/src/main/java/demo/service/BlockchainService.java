package demo.service;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import demo.config.BlockchainProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockchainService {
    private final BlockchainProperties properties;

    public BlockchainService(BlockchainProperties properties) {
        this.properties = properties;
    }

    public String invoke(String userAddress, String functionName, List<?> functionParameters) {
        JSONObject request = new JSONObject();
        request.set("contractName", properties.getContractName());
        request.set("contractAddress", properties.getContractAddress());
        request.set("contractAbi", JSONUtil.parseArray(properties.getContractAbi()));
        request.set("user", userAddress);
        request.set("funcName", functionName);
        request.set("funcParam", functionParameters);
        request.set("groupId", properties.getGroupId());
        request.set("useCns", properties.isUseCns());
        return HttpRequest.post(properties.getWebaseUrl())
                .header(Header.CONTENT_TYPE, "application/json")
                .timeout(properties.getTimeoutMs())
                .body(JSONUtil.toJsonStr(request))
                .execute()
                .body();
    }

    public JSONArray getAllFood() {
        return JSONUtil.parseArray(invoke(properties.getProducerAddress(), "getAllFood", new JSONArray()));
    }

    public JSONArray getFood(String traceNumber) {
        return JSONUtil.parseArray(invoke(properties.getProducerAddress(), "getFood", singleParameter(traceNumber)));
    }

    public JSONArray getTraceInfo(String traceNumber) {
        return JSONUtil.parseArray(invoke(properties.getProducerAddress(), "getTraceInfo", singleParameter(traceNumber)));
    }

    public String getProducerAddress() { return properties.getProducerAddress(); }
    public String getDistributorAddress() { return properties.getDistributorAddress(); }
    public String getRetailerAddress() { return properties.getRetailerAddress(); }

    private JSONArray singleParameter(String traceNumber) {
        JSONArray parameters = new JSONArray();
        parameters.add(Long.parseLong(traceNumber));
        return parameters;
    }
}
