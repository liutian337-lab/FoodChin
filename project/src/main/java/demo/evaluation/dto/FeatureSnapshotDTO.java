package demo.evaluation.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FeatureSnapshotDTO {
    @NotBlank @Size(max = 128) private String featureName;
    @NotBlank @Size(max = 512) private String featureValue;
    @Size(max = 128) private String source;
    public String getFeatureName() { return featureName; }
    public void setFeatureName(String featureName) { this.featureName = featureName; }
    public String getFeatureValue() { return featureValue; }
    public void setFeatureValue(String featureValue) { this.featureValue = featureValue; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
