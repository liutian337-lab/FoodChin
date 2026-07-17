package demo.application;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import demo.dto.request.FoodProduceRequest;
import demo.service.FoodService;
import demo.vo.FoodDetailVO;
import demo.vo.TraceabilityVO;
import org.springframework.stereotype.Service;

@Service
public class FoodApplicationService {
    private final FoodService foodService;

    public FoodApplicationService(FoodService foodService) {
        this.foodService = foodService;
    }

    public String userInfo(String userName) {
        return foodService.userInfo(userName);
    }

    public String produce(FoodProduceRequest request) {
        JSONObject payload = new JSONObject();
        if (request != null) {
            payload.set("traceNumber", request.getTraceNumber());
            payload.set("foodName", request.getFoodName());
            payload.set("traceName", request.getTraceName());
            payload.set("quality", request.getQuality());
        }
        return foodService.produce(payload);
    }

    public String foodList() {
        JSONArray legacyItems = JSONUtil.parseArray(foodService.foodList());
        JSONArray response = new JSONArray();
        for (Object item : legacyItems) {
            JSONObject food = JSONUtil.parseObj(item);
            response.add(JSONUtil.toJsonStr(FoodDetailVO.fromJson(food).toLegacyJson()));
        }
        return JSONUtil.toJsonStr(response);
    }

    public String food(String traceNumber) {
        JSONObject food = JSONUtil.parseObj(foodService.food(traceNumber));
        return JSONUtil.toJsonStr(FoodDetailVO.fromJson(food).toLegacyJson());
    }

    public String producing() {
        JSONArray legacyItems = JSONUtil.parseArray(foodService.producing());
        JSONArray response = new JSONArray();
        for (Object item : legacyItems) {
            response.add(TraceabilityVO.fromJson(JSONUtil.parseObj(item)).toLegacyJson());
        }
        return JSONUtil.toJsonStr(response);
    }
}
