package demo.vo;

import cn.hutool.json.JSONObject;

public class TraceabilityVO {
    private String traceNumber;
    private String name;
    private Object produceTime;
    private Object timestamp;
    private String from;
    private String to;
    private Object quality;
    private String fromAddress;
    private String toAddress;

    public static TraceabilityVO fromJson(JSONObject source) {
        TraceabilityVO vo = new TraceabilityVO();
        vo.setTraceNumber(source.getStr("traceNumber"));
        vo.setName(source.getStr("name"));
        vo.setProduceTime(source.get("produce_time"));
        vo.setTimestamp(source.get("timestamp"));
        vo.setFrom(source.getStr("from"));
        vo.setTo(source.getStr("to"));
        vo.setQuality(source.get("quality"));
        vo.setFromAddress(source.getStr("from_address"));
        vo.setToAddress(source.getStr("to_address"));
        return vo;
    }

    public JSONObject toLegacyJson() {
        JSONObject json = new JSONObject();
        json.set("traceNumber", traceNumber);
        json.set("name", name);
        json.set("produce_time", produceTime);
        json.set("timestamp", timestamp);
        json.set("from", from);
        json.set("quality", quality);
        json.set("from_address", fromAddress);
        if (to != null) json.set("to", to);
        if (toAddress != null) json.set("to_address", toAddress);
        return json;
    }

    public String getTraceNumber() { return traceNumber; }
    public void setTraceNumber(String traceNumber) { this.traceNumber = traceNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Object getProduceTime() { return produceTime; }
    public void setProduceTime(Object produceTime) { this.produceTime = produceTime; }
    public Object getTimestamp() { return timestamp; }
    public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public Object getQuality() { return quality; }
    public void setQuality(Object quality) { this.quality = quality; }
    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
    public String getToAddress() { return toAddress; }
    public void setToAddress(String toAddress) { this.toAddress = toAddress; }
}
