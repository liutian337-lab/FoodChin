package demo.infrastructure.ai;

public interface AIClient {
    PredictionResponse predict(FoodFeatureRequest request);
}
