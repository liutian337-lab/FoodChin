package demo.evaluation.dto;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EvaluationCreateDTO {
    @NotNull @Positive private Long foodId;
    @NotNull @DecimalMin("0.0") @DecimalMax("100.0") private BigDecimal score;
    @NotBlank @Size(max = 16) private String level;
    @DecimalMin("0.0") @DecimalMax("1.0") private BigDecimal confidence;
    private Long modelId;
    @NotBlank @Size(max = 64) private String modelVersion;
    private LocalDateTime evaluationTime;
    @Valid private List<FeatureSnapshotDTO> featureSnapshots = new ArrayList<FeatureSnapshotDTO>();
    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    public LocalDateTime getEvaluationTime() { return evaluationTime; }
    public void setEvaluationTime(LocalDateTime evaluationTime) { this.evaluationTime = evaluationTime; }
    public List<FeatureSnapshotDTO> getFeatureSnapshots() { return featureSnapshots; }
    public void setFeatureSnapshots(List<FeatureSnapshotDTO> featureSnapshots) { this.featureSnapshots = featureSnapshots; }
}
