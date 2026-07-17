package demo.application;

import cn.hutool.json.JSONObject;
import demo.dto.request.TraceUpdateRequest;
import demo.service.TraceService;
import org.springframework.stereotype.Service;

@Service
public class TraceApplicationService {
    private final TraceService traceService;

    public TraceApplicationService(TraceService traceService) {
        this.traceService = traceService;
    }

    public String trace(String traceNumber) {
        return traceService.trace(traceNumber);
    }

    public String addDistribution(TraceUpdateRequest request) {
        return traceService.addDistribution(toPayload(request));
    }

    public String addRetail(TraceUpdateRequest request) {
        return traceService.addRetail(toPayload(request));
    }

    public String latestTraceList() {
        return traceService.latestTraceList();
    }

    public String distributing() {
        return traceService.distributing();
    }

    public String retailing() {
        return traceService.retailing();
    }

    private JSONObject toPayload(TraceUpdateRequest request) {
        JSONObject payload = new JSONObject();
        if (request != null) {
            payload.set("traceNumber", request.getTraceNumber());
            payload.set("traceName", request.getTraceName());
            payload.set("quality", request.getQuality());
        }
        return payload;
    }
}
