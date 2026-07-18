import os
from functools import lru_cache
from pathlib import Path

from app.model.random_forest_model import RandomForestPredictionModel


@lru_cache(maxsize=1)
def get_prediction_model() -> RandomForestPredictionModel:
    service_root = Path(__file__).resolve().parents[2]
    configured_path = os.getenv("AI_MODEL_PATH")
    model_path = Path(configured_path).expanduser() if configured_path else service_root / "models" / "random_forest.joblib"
    if not model_path.is_absolute():
        model_path = service_root / model_path
    return RandomForestPredictionModel.load(str(model_path))
