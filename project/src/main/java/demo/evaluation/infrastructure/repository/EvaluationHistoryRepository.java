package demo.evaluation.infrastructure.repository;
import demo.evaluation.infrastructure.entity.EvaluationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EvaluationHistoryRepository extends JpaRepository<EvaluationHistoryEntity, Long> {
    List<EvaluationHistoryEntity> findByEvaluationIdOrderByCreatedTimeAsc(Long evaluationId);
}
