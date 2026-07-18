package demo.evaluation.infrastructure.repository;
import demo.evaluation.infrastructure.entity.FeatureSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FeatureSnapshotRepository extends JpaRepository<FeatureSnapshotEntity, Long> { }
