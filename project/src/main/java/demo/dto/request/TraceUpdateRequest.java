package demo.dto.request;

public class TraceUpdateRequest {
    private Integer traceNumber;
    private String traceName;
    private Integer quality;
    public Integer getTraceNumber() { return traceNumber; }
    public void setTraceNumber(Integer traceNumber) { this.traceNumber = traceNumber; }
    public String getTraceName() { return traceName; }
    public void setTraceName(String traceName) { this.traceName = traceName; }
    public Integer getQuality() { return quality; }
    public void setQuality(Integer quality) { this.quality = quality; }
}
