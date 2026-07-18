package demo.evaluation.dto;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class EvaluationPredictDTO {
    @NotNull @Positive
    private Long foodId;
    @NotNull
    private Map<String, BigDecimal> features = new LinkedHashMap<String, BigDecimal>();

    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }
    public Map<String, BigDecimal> getFeatures() { return features; }
    public void setFeatures(Map<String, BigDecimal> features) { this.features = features; }
}
