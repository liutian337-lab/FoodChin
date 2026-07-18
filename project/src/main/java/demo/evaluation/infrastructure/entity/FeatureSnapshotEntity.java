package demo.evaluation.infrastructure.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feature_snapshot")
public class FeatureSnapshotEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "evaluation_id", nullable = false) private Long evaluationId;
    @Column(name = "food_id", nullable = false) private Long foodId;
    @Column(name = "feature_name", nullable = false, length = 128) private String featureName;
    @Column(name = "feature_value", nullable = false, length = 512) private String featureValue;
    @Column(length = 128) private String source;
    @Column(name = "created_time", nullable = false) private LocalDateTime createdTime;
    public Long getId() { return id; }
    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }
    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }
    public String getFeatureName() { return featureName; }
    public void setFeatureName(String featureName) { this.featureName = featureName; }
    public String getFeatureValue() { return featureValue; }
    public void setFeatureValue(String featureValue) { this.featureValue = featureValue; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
