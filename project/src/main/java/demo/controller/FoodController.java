package demo.controller;

import cn.hutool.json.JSONObject;
import demo.service.FoodService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FoodController {
    private final FoodService foodService;
    public FoodController(FoodService foodService) { this.foodService = foodService; }

    @GetMapping("/index")
    public String index() { return "index"; }

    @ResponseBody
    @GetMapping(path = "/userinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public String userInfo(String userName) { return foodService.userInfo(userName); }

    @ResponseBody
    @PostMapping(path = "/produce", produces = MediaType.APPLICATION_JSON_VALUE)
    public String produce(@RequestBody JSONObject request) { return foodService.produce(request); }

    @ResponseBody
    @GetMapping(path = "/foodlist", produces = MediaType.APPLICATION_JSON_VALUE)
    public String foodList() { return foodService.foodList(); }

    @ResponseBody
    @GetMapping(path = "/food", produces = MediaType.APPLICATION_JSON_VALUE)
    public String food(String traceNumber) { return foodService.food(traceNumber); }

    @ResponseBody
    @GetMapping(path = "/producing", produces = MediaType.APPLICATION_JSON_VALUE)
    public String producing() { return foodService.producing(); }
}
