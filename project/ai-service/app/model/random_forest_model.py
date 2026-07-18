from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Any, Dict, List, Optional

import joblib
import numpy as np

from app.model.prediction_model import PredictionModel
from app.schema.prediction import FoodFeatureRequest, PredictionResponse


class ModelUnavailableError(RuntimeError):
    pass


@dataclass(frozen=True)
class ModelMetadata:
    model_name: str
    version: str
    algorithm: str
    accuracy: Optional[float]
    feature_names: List[str]


class RandomForestPredictionModel(PredictionModel):
    """Loads a persisted sklearn RandomForestRegressor and predicts a 0-100 score."""

    def __init__(self, model: Any, metadata: ModelMetadata):
        self._model = model
        self._metadata = metadata

    @classmethod
    def load(cls, model_path: str) -> "RandomForestPredictionModel":
        path = Path(model_path)
        if not path.is_file():
            raise ModelUnavailableError(f"Random Forest model artifact does not exist: {path}")
        artifact: Dict[str, Any] = joblib.load(path)
        required = {"model", "feature_names", "model_name", "version", "algorithm"}
        missing = required.difference(artifact)
        if missing:
            raise ModelUnavailableError("Invalid model artifact; missing keys: " + ", ".join(sorted(missing)))
        return cls(artifact["model"], ModelMetadata(
            model_name=str(artifact["model_name"]), version=str(artifact["version"]),
            algorithm=str(artifact["algorithm"]), accuracy=artifact.get("accuracy"),
            feature_names=list(artifact["feature_names"])))

    def predict(self, request: FoodFeatureRequest) -> PredictionResponse:
        missing = [name for name in self._metadata.feature_names if name not in request.features]
        if missing:
            raise ValueError("Missing required model features: " + ", ".join(missing))
        vector = np.array([[request.features[name] for name in self._metadata.feature_names]], dtype=float)
        score = float(np.clip(self._model.predict(vector)[0], 0.0, 100.0))
        predictions = np.array([tree.predict(vector)[0] for tree in self._model.estimators_], dtype=float)
        # Lower disagreement among trees means a higher confidence score.
        confidence = float(np.clip(1.0 - np.std(predictions) / 100.0, 0.0, 1.0))
        return PredictionResponse(score=round(score, 2), level=_level(score), confidence=round(confidence, 4),
                                  modelVersion=self._metadata.version, modelName=self._metadata.model_name,
                                  algorithm=self._metadata.algorithm, accuracy=self._metadata.accuracy)


def _level(score: float) -> str:
    return "A" if score >= 90 else "B" if score >= 80 else "C" if score >= 60 else "D"
