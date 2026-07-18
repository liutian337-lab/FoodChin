package demo.evaluation.infrastructure.repository;
import demo.evaluation.infrastructure.entity.ModelInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ModelInfoRepository extends JpaRepository<ModelInfoEntity, Long> {
    Optional<ModelInfoEntity> findByModelNameAndVersion(String modelName, String version);
}
