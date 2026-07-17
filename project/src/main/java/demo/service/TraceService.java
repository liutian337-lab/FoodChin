package demo.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

@Service
public class TraceService {
    private final FoodService foodService;
    private final BlockchainService blockchainService;

    public TraceService(FoodService foodService, BlockchainService blockchainService) {
        this.foodService = foodService;
        this.blockchainService = blockchainService;
    }

    public String trace(String traceNumber) {
        FoodService.validateTraceNumber(traceNumber);
        return JSONUtil.toJsonStr(foodService.traceRecords(traceNumber));
    }

    public String addDistribution(JSONObject request) {
        return addTrace(request, blockchainService.getDistributorAddress(), "addTraceInfoByDistributor", "分销信息添加成功");
    }

    public String addRetail(JSONObject request) {
        return addTrace(request, blockchainService.getRetailerAddress(), "addTraceInfoByRetailer", "零售信息添加成功");
    }

    public String latestTraceList() {
        JSONArray result = new JSONArray();
        for (Object identifier : foodService.foodIdentifiers()) {
            JSONArray records = foodService.traceRecords(identifier.toString());
            if (!records.isEmpty()) result.add(records.get(records.size() - 1));
        }
        return JSONUtil.toJsonStr(result);
    }

    public String distributing() { return recordsWithSize(2, 1); }
    public String retailing() { return recordsWithSize(3, 2); }

    private String recordsWithSize(int requiredSize, int recordIndex) {
        JSONArray result = new JSONArray();
        for (Object identifier : foodService.foodIdentifiers()) {
            JSONArray records = foodService.traceRecords(identifier.toString());
            if (records.size() == requiredSize) result.add(records.get(recordIndex));
        }
        return JSONUtil.toJsonStr(result);
    }

    private String addTrace(JSONObject request, String account, String functionName, String successMessage) {
        JSONObject output = new JSONObject();
        try {
            int traceNumber = FoodService.positiveInt(request, "traceNumber");
            String traceName = FoodService.requiredString(request, "traceName");
            int quality = FoodService.quality(request);
            JSONArray parameters = new JSONArray();
            parameters.add(traceNumber); parameters.add(traceName); parameters.add(quality);
            JSONObject chainResponse = JSONUtil.parseObj(blockchainService.invoke(account, functionName, parameters));
            if ("Success".equals(chainResponse.getStr("message"))) {
                output.set("code", 200); output.set("ret", 1); output.set("msg", successMessage);
            } else {
                output.set("code", 500); output.set("ret", 0);
                output.set("msg", chainResponse.getStr("message", "合约调用失败"));
            }
        } catch (IllegalArgumentException exception) {
            output.set("code", 400); output.set("ret", 0); output.set("msg", exception.getMessage());
        } catch (Exception exception) {
            output.set("code", 500); output.set("ret", 0); output.set("msg", "系统错误: " + exception.getMessage());
        }
        return JSONUtil.toJsonStr(output);
    }
}
