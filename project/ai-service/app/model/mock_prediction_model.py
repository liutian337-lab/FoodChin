from app.model.prediction_model import PredictionModel
from app.schema.prediction import FoodFeatureRequest, PredictionResponse


class MockPredictionModel(PredictionModel):
    """Deterministic transport stub; replace with a trained model implementation later."""

    def predict(self, request: FoodFeatureRequest) -> PredictionResponse:
        values = list(request.features.values())
        average = sum(values) / len(values) if values else 0.0
        score = round(min(100.0, max(0.0, 80.0 + average)), 2)
        level = "A" if score >= 90 else "B" if score >= 80 else "C" if score >= 60 else "D"
        return PredictionResponse(score=score, level=level, confidence=0.85, modelVersion="mock-v1", modelName="mock", algorithm="Mock", accuracy=None)
