package demo.controller;

import demo.application.FoodApplicationService;
import demo.dto.request.FoodProduceRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FoodController {
    private final FoodApplicationService foodApplicationService;
    public FoodController(FoodApplicationService foodApplicationService) { this.foodApplicationService = foodApplicationService; }

    @GetMapping("/index")
    public String index() { return "index"; }

    @ResponseBody
    @GetMapping(path = "/userinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public String userInfo(String userName) { return foodApplicationService.userInfo(userName); }

    @ResponseBody
    @PostMapping(path = "/produce", produces = MediaType.APPLICATION_JSON_VALUE)
    public String produce(@RequestBody FoodProduceRequest request) { return foodApplicationService.produce(request); }

    @ResponseBody
    @GetMapping(path = "/foodlist", produces = MediaType.APPLICATION_JSON_VALUE)
    public String foodList() { return foodApplicationService.foodList(); }

    @ResponseBody
    @GetMapping(path = "/food", produces = MediaType.APPLICATION_JSON_VALUE)
    public String food(String traceNumber) { return foodApplicationService.food(traceNumber); }

    @ResponseBody
    @GetMapping(path = "/producing", produces = MediaType.APPLICATION_JSON_VALUE)
    public String producing() { return foodApplicationService.producing(); }
}
