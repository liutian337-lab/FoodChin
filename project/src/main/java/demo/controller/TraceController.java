package demo.controller;

import demo.application.TraceApplicationService;
import demo.dto.request.TraceUpdateRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TraceController {
    private final TraceApplicationService traceApplicationService;
    public TraceController(TraceApplicationService traceApplicationService) { this.traceApplicationService = traceApplicationService; }

    @ResponseBody
    @GetMapping(path = "/trace", produces = MediaType.APPLICATION_JSON_VALUE)
    public String trace(String traceNumber) { return traceApplicationService.trace(traceNumber); }

    @ResponseBody
    @PostMapping(path = "/adddistribution", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addDistribution(@RequestBody TraceUpdateRequest request) { return traceApplicationService.addDistribution(request); }

    @ResponseBody
    @PostMapping(path = "/addretail", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addRetail(@RequestBody TraceUpdateRequest request) { return traceApplicationService.addRetail(request); }

    @ResponseBody
    @GetMapping(path = "/newtracelist", produces = MediaType.APPLICATION_JSON_VALUE)
    public String latestTraceList() { return traceApplicationService.latestTraceList(); }

    @ResponseBody
    @GetMapping(path = "/distributing", produces = MediaType.APPLICATION_JSON_VALUE)
    public String distributing() { return traceApplicationService.distributing(); }

    @ResponseBody
    @GetMapping(path = "/retailing", produces = MediaType.APPLICATION_JSON_VALUE)
    public String retailing() { return traceApplicationService.retailing(); }
}
