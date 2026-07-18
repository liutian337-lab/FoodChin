from abc import ABC, abstractmethod

from app.schema.prediction import FoodFeatureRequest, PredictionResponse


class PredictionModel(ABC):
    @abstractmethod
    def predict(self, request: FoodFeatureRequest) -> PredictionResponse:
        """Return a food-quality prediction for validated feature input."""
