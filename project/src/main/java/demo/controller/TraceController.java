package demo.controller;

import cn.hutool.json.JSONObject;
import demo.service.TraceService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TraceController {
    private final TraceService traceService;
    public TraceController(TraceService traceService) { this.traceService = traceService; }

    @ResponseBody
    @GetMapping(path = "/trace", produces = MediaType.APPLICATION_JSON_VALUE)
    public String trace(String traceNumber) { return traceService.trace(traceNumber); }

    @ResponseBody
    @PostMapping(path = "/adddistribution", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addDistribution(@RequestBody JSONObject request) { return traceService.addDistribution(request); }

    @ResponseBody
    @PostMapping(path = "/addretail", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addRetail(@RequestBody JSONObject request) { return traceService.addRetail(request); }

    @ResponseBody
    @GetMapping(path = "/newtracelist", produces = MediaType.APPLICATION_JSON_VALUE)
    public String latestTraceList() { return traceService.latestTraceList(); }

    @ResponseBody
    @GetMapping(path = "/distributing", produces = MediaType.APPLICATION_JSON_VALUE)
    public String distributing() { return traceService.distributing(); }

    @ResponseBody
    @GetMapping(path = "/retailing", produces = MediaType.APPLICATION_JSON_VALUE)
    public String retailing() { return traceService.retailing(); }
}
