package demo.evaluation.infrastructure.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blockchain_transaction", uniqueConstraints = {
        @UniqueConstraint(name = "uk_blockchain_business", columnNames = {"business_type", "business_id"}),
        @UniqueConstraint(name = "uk_blockchain_transaction_hash", columnNames = "transaction_hash")})
public class BlockchainTransactionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "business_type", nullable = false, length = 64) private String businessType;
    @Column(name = "business_id", nullable = false) private Long businessId;
    @Column(name = "food_id") private Long foodId;
    @Column(name = "transaction_hash", length = 128) private String transactionHash;
    @Column(name = "contract_address", nullable = false, length = 128) private String contractAddress;
    @Column(name = "block_number") private Long blockNumber;
    @Column(nullable = false, length = 32) private String status;
    @Column(name = "request_hash", nullable = false, length = 128) private String requestHash;
    @Column(name = "evaluation_hash", nullable = false, length = 64) private String evaluationHash;
    @Column(name = "retry_count", nullable = false) private Integer retryCount;
    @Column(name = "last_error", length = 1000) private String lastError;
    @Column(name = "created_time", nullable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    public Long getId() { return id; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }
    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }
    public String getTransactionHash() { return transactionHash; }
    public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    public String getContractAddress() { return contractAddress; }
    public void setContractAddress(String contractAddress) { this.contractAddress = contractAddress; }
    public Long getBlockNumber() { return blockNumber; }
    public void setBlockNumber(Long blockNumber) { this.blockNumber = blockNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRequestHash() { return requestHash; }
    public void setRequestHash(String requestHash) { this.requestHash = requestHash; }
    public String getEvaluationHash() { return evaluationHash; }
    public void setEvaluationHash(String evaluationHash) { this.evaluationHash = evaluationHash; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}
