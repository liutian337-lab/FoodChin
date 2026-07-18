package demo.evaluation.infrastructure.repository;
import demo.evaluation.infrastructure.entity.BlockchainTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface BlockchainTransactionRepository extends JpaRepository<BlockchainTransactionEntity, Long> {
    Optional<BlockchainTransactionEntity> findByBusinessTypeAndBusinessId(String businessType, Long businessId);
}
