package demo.evaluation.infrastructure.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "model_info", uniqueConstraints = @UniqueConstraint(name = "uk_model_info_name_version", columnNames = {"model_name", "version"}))
public class ModelInfoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "model_name", nullable = false, length = 128) private String modelName;
    @Column(nullable = false, length = 64) private String version;
    @Column(nullable = false, length = 64) private String algorithm;
    @Column(precision = 6, scale = 5) private BigDecimal accuracy;
    @Lob @Column(name = "metrics_json") private String metricsJson;
    @Column(name = "created_time", nullable = false) private LocalDateTime createdTime;
    public Long getId() { return id; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    public BigDecimal getAccuracy() { return accuracy; }
    public void setAccuracy(BigDecimal accuracy) { this.accuracy = accuracy; }
    public String getMetricsJson() { return metricsJson; }
    public void setMetricsJson(String metricsJson) { this.metricsJson = metricsJson; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
