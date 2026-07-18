package demo.evaluation.vo;

import demo.evaluation.infrastructure.entity.EvaluationHistoryEntity;
import java.time.LocalDateTime;

public class EvaluationHistoryVO {
    private Long id;
    private Long evaluationId;
    private String operation;
    private String description;
    private LocalDateTime createdTime;
    public static EvaluationHistoryVO from(EvaluationHistoryEntity entity) {
        EvaluationHistoryVO vo = new EvaluationHistoryVO();
        vo.id = entity.getId(); vo.evaluationId = entity.getEvaluationId(); vo.operation = entity.getOperation();
        vo.description = entity.getDescription(); vo.createdTime = entity.getCreatedTime();
        return vo;
    }
    public Long getId() { return id; }
    public Long getEvaluationId() { return evaluationId; }
    public String getOperation() { return operation; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedTime() { return createdTime; }
}
