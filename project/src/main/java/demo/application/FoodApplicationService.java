package demo.application;

import cn.hutool.json.JSONObject;
import demo.dto.request.FoodProduceRequest;
import demo.service.FoodService;
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
        return foodService.foodList();
    }

    public String food(String traceNumber) {
        return foodService.food(traceNumber);
    }

    public String producing() {
        return foodService.producing();
    }
}
