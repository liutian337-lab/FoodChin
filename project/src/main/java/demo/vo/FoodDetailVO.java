package demo.vo;

import cn.hutool.json.JSONObject;

public class FoodDetailVO {
    private String traceNumber;
    private Object timestamp;
    private String produce;
    private String name;
    private String current;
    private String address;
    private Object quality;
    private Integer status;

    public static FoodDetailVO fromJson(JSONObject source) {
        FoodDetailVO vo = new FoodDetailVO();
        vo.setTraceNumber(source.getStr("traceNumber"));
        vo.setTimestamp(source.get("timestamp"));
        vo.setProduce(source.getStr("produce"));
        vo.setName(source.getStr("name"));
        vo.setCurrent(source.getStr("current"));
        vo.setAddress(source.getStr("address"));
        vo.setQuality(source.get("quality"));
        vo.setStatus(source.getInt("status"));
        return vo;
    }

    public JSONObject toLegacyJson() {
        JSONObject json = new JSONObject();
        json.set("traceNumber", traceNumber);
        json.set("timestamp", timestamp);
        json.set("produce", produce);
        json.set("name", name);
        json.set("current", current);
        json.set("address", address);
        json.set("quality", quality);
        json.set("status", status);
        return json;
    }

    public String getTraceNumber() { return traceNumber; }
    public void setTraceNumber(String traceNumber) { this.traceNumber = traceNumber; }
    public Object getTimestamp() { return timestamp; }
    public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }
    public String getProduce() { return produce; }
    public void setProduce(String produce) { this.produce = produce; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCurrent() { return current; }
    public void setCurrent(String current) { this.current = current; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Object getQuality() { return quality; }
    public void setQuality(Object quality) { this.quality = quality; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
