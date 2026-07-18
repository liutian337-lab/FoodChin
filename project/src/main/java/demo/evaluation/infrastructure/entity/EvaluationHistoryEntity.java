package demo.evaluation.infrastructure.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_history")
public class EvaluationHistoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "evaluation_id", nullable = false) private Long evaluationId;
    @Column(nullable = false, length = 64) private String operation;
    @Column(length = 500) private String description;
    @Column(name = "created_time", nullable = false) private LocalDateTime createdTime;
    public Long getId() { return id; }
    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
