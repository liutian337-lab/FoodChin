package demo.dto.request;

public class FoodProduceRequest {
    private Integer traceNumber;
    private String foodName;
    private String traceName;
    private Integer quality;
    public Integer getTraceNumber() { return traceNumber; }
    public void setTraceNumber(Integer traceNumber) { this.traceNumber = traceNumber; }
    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public String getTraceName() { return traceName; }
    public void setTraceName(String traceName) { this.traceName = traceName; }
    public Integer getQuality() { return quality; }
    public void setQuality(Integer quality) { this.quality = quality; }
}
