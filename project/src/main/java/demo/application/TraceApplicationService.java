package demo.application;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import demo.dto.request.TraceUpdateRequest;
import demo.service.TraceService;
import demo.vo.TraceabilityVO;
import org.springframework.stereotype.Service;

@Service
public class TraceApplicationService {
    private final TraceService traceService;

    public TraceApplicationService(TraceService traceService) {
        this.traceService = traceService;
    }

    public String trace(String traceNumber) {
        return toTraceabilityResponse(traceService.trace(traceNumber));
    }

    public String addDistribution(TraceUpdateRequest request) {
        return traceService.addDistribution(toPayload(request));
    }

    public String addRetail(TraceUpdateRequest request) {
        return traceService.addRetail(toPayload(request));
    }

    public String latestTraceList() {
        return toTraceabilityResponse(traceService.latestTraceList());
    }

    public String distributing() {
        return toTraceabilityResponse(traceService.distributing());
    }

    public String retailing() {
        return toTraceabilityResponse(traceService.retailing());
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

    private String toTraceabilityResponse(String legacyResponse) {
        JSONArray legacyItems = JSONUtil.parseArray(legacyResponse);
        JSONArray response = new JSONArray();
        for (Object item : legacyItems) {
            response.add(TraceabilityVO.fromJson(JSONUtil.parseObj(item)).toLegacyJson());
        }
        return JSONUtil.toJsonStr(response);
    }
}
