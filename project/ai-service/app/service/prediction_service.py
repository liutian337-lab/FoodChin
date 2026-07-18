from typing import Optional

from app.model.model_registry import get_prediction_model
from app.model.prediction_model import PredictionModel
from app.schema.prediction import FoodFeatureRequest, PredictionResponse


class PredictionService:
    def __init__(self, model: Optional[PredictionModel] = None):
        self._model = model

    def predict(self, request: FoodFeatureRequest) -> PredictionResponse:
        return (self._model or get_prediction_model()).predict(request)
