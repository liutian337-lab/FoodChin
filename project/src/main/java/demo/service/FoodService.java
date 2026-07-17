package demo.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import demo.exception.BusinessException;
import demo.infrastructure.blockchain.BlockchainAccount;
import demo.infrastructure.blockchain.BlockchainService;
import org.springframework.stereotype.Service;

@Service
public class FoodService {
    private final BlockchainService blockchainService;

    public FoodService(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    public String userInfo(String userName) {
        JSONObject output = new JSONObject();
        if ("producer".equals(userName)) output.set("address", blockchainService.accountAddress(BlockchainAccount.PRODUCER));
        else if ("distributor".equals(userName)) output.set("address", blockchainService.accountAddress(BlockchainAccount.DISTRIBUTOR));
        else if ("retailer".equals(userName)) output.set("address", blockchainService.accountAddress(BlockchainAccount.RETAILER));
        else output.set("error", "user not found");
        return JSONUtil.toJsonStr(output);
    }

    public String produce(JSONObject request) {
        JSONObject output = new JSONObject();
        try {
            int traceNumber = positiveInt(request, "traceNumber");
            String foodName = requiredString(request, "foodName");
            String traceName = requiredString(request, "traceName");
            int quality = quality(request);
            JSONArray parameters = new JSONArray();
            parameters.add(foodName);
            parameters.add(traceNumber);
            parameters.add(traceName);
            parameters.add(quality);
            JSONObject chainResponse = JSONUtil.parseObj(blockchainService.write(
                    BlockchainAccount.PRODUCER, "newFood", parameters));
            if ("Success".equals(chainResponse.getStr("message"))) {
                output.set("code", 200); output.set("ret", 1); output.set("msg", "食品添加成功"); output.set("data", chainResponse);
            } else {
                output.set("code", 500); output.set("ret", 0); output.set("msg", messageOrDefault(chainResponse));
            }
        } catch (IllegalArgumentException exception) {
            output.set("code", 400); output.set("ret", 0); output.set("msg", exception.getMessage());
        } catch (Exception exception) {
            output.set("code", 500); output.set("ret", 0); output.set("msg", "系统错误: " + exception.getMessage());
        }
        return JSONUtil.toJsonStr(output);
    }

    public String foodList() {
        JSONArray identifiers = foodIdentifiers();
        JSONArray result = new JSONArray();
        for (Object identifier : identifiers) result.add(food(identifier.toString()));
        return JSONUtil.toJsonStr(result);
    }

    public String food(String traceNumber) {
        validateTraceNumber(traceNumber);
        return foodInternal(traceNumber);
    }

    public String producing() {
        JSONArray result = new JSONArray();
        for (Object identifier : foodIdentifiers()) {
            JSONArray trace = traceRecords(identifier.toString());
            if (trace.size() == 1) result.add(trace.get(0));
        }
        return JSONUtil.toJsonStr(result);
    }

    String foodInternal(String traceNumber) {
        JSONArray food = blockchainService.queryFood(traceNumber);
        JSONArray traceInfo = blockchainService.queryTraceInfo(traceNumber);
        int records = traceInfo.isEmpty() ? 0 : traceInfo.getJSONArray(0).size();
        JSONObject output = new JSONObject();
        output.set("traceNumber", traceNumber);
        output.set("timestamp", food.get(0));
        output.set("produce", food.get(1));
        output.set("name", food.get(2));
        output.set("current", food.get(3));
        output.set("address", food.get(4));
        output.set("quality", food.get(5));
        output.set("status", records == 1 ? 0 : records == 2 ? 1 : 2);
        return JSONUtil.toJsonStr(output);
    }

    JSONArray foodIdentifiers() {
        JSONArray response = blockchainService.queryAllFood();
        return response.getJSONArray(0);
    }

    JSONArray traceRecords(String traceNumber) {
        JSONArray food = blockchainService.queryFood(traceNumber);
        JSONArray traceInfo = blockchainService.queryTraceInfo(traceNumber);
        JSONArray timestamps = traceInfo.getJSONArray(0);
        JSONArray names = traceInfo.getJSONArray(1);
        JSONArray addresses = traceInfo.getJSONArray(2);
        JSONArray qualities = traceInfo.getJSONArray(3);
        JSONArray output = new JSONArray();
        for (int index = 0; index < timestamps.size(); index++) {
            JSONObject record = new JSONObject();
            record.set("traceNumber", traceNumber);
            record.set("name", food.get(2));
            record.set("produce_time", food.get(0));
            record.set("timestamp", timestamps.get(index));
            record.set("quality", qualities.get(index));
            if (index == 0) {
                record.set("from", names.get(index));
                record.set("from_address", addresses.get(index));
            } else {
                record.set("from", names.get(index - 1)); record.set("to", names.get(index));
                record.set("from_address", addresses.get(index - 1)); record.set("to_address", addresses.get(index));
            }
            output.add(record);
        }
        return output;
    }

    static void validateTraceNumber(String value) {
        try {
            if (value == null || Integer.parseInt(value) <= 0) throw new BusinessException("invalid parameter");
        } catch (NumberFormatException exception) { throw new BusinessException("invalid parameter"); }
    }

    static int positiveInt(JSONObject request, String field) {
        if (request == null || !request.containsKey(field)) throw new BusinessException(field + " 是必填字段");
        Object value = request.get(field);
        try {
            long number = Long.parseLong(String.valueOf(value));
            if (number <= 0 || number > Integer.MAX_VALUE) throw new BusinessException(field + " 必须是有效正整数");
            return (int) number;
        } catch (NumberFormatException exception) { throw new BusinessException(field + " 必须是有效正整数"); }
    }

    static String requiredString(JSONObject request, String field) {
        String value = request == null ? null : request.getStr(field);
        if (value == null || value.trim().isEmpty()) throw new BusinessException(field + " 是必填字段");
        return value;
    }

    static int quality(JSONObject request) {
        int value = positiveOrZeroInt(request, "quality");
        if (value > 2) throw new BusinessException("quality 必须是 0、1 或 2");
        return value;
    }

    private static int positiveOrZeroInt(JSONObject request, String field) {
        if (request == null || !request.containsKey(field)) throw new BusinessException(field + " 是必填字段");
        try {
            int value = Integer.parseInt(String.valueOf(request.get(field)));
            if (value < 0) throw new BusinessException(field + " 必须是 0、1 或 2");
            return value;
        } catch (NumberFormatException exception) { throw new BusinessException(field + " 必须是 0、1 或 2"); }
    }

    private String messageOrDefault(JSONObject response) {
        String message = response.getStr("message");
        return message == null ? "合约调用失败" : message;
    }
}
