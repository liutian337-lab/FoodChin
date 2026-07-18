package demo.evaluation.infrastructure.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_evaluation")
public class FoodEvaluationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "food_id", nullable = false)
    private Long foodId;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;
    @Column(nullable = false, length = 16)
    private String level;
    @Column(precision = 5, scale = 4)
    private BigDecimal confidence;
    @Column(name = "model_id")
    private Long modelId;
    @Column(name = "model_version", nullable = false, length = 64)
    private String modelVersion;
    @Column(name = "evaluation_time", nullable = false)
    private LocalDateTime evaluationTime;
    @Column(name = "chain_status", nullable = false, length = 32)
    private String chainStatus;
    @Column(name = "evaluation_hash", nullable = false, length = 64)
    private String evaluationHash;
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    public Long getId() { return id; }
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
    public String getChainStatus() { return chainStatus; }
    public void setChainStatus(String chainStatus) { this.chainStatus = chainStatus; }
    public String getEvaluationHash() { return evaluationHash; }
    public void setEvaluationHash(String evaluationHash) { this.evaluationHash = evaluationHash; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
