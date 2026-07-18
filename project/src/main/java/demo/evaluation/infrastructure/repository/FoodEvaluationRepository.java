package demo.evaluation.infrastructure.repository;
import demo.evaluation.infrastructure.entity.FoodEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface FoodEvaluationRepository extends JpaRepository<FoodEvaluationEntity, Long> {
    List<FoodEvaluationEntity> findByFoodIdOrderByEvaluationTimeDesc(Long foodId);
    Optional<FoodEvaluationEntity> findFirstByFoodIdOrderByEvaluationTimeDesc(Long foodId);
}
