"""Train and persist the FoodChin Random Forest regression model from labeled CSV data."""
import argparse
from datetime import datetime, timezone
from pathlib import Path

import joblib
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split


def grade(score):
    return "A" if score >= 90 else "B" if score >= 80 else "C" if score >= 60 else "D"


def main():
    service_root = Path(__file__).resolve().parent
    parser = argparse.ArgumentParser()
    parser.add_argument("--data", default=str(service_root / "data" / "bootstrap_training.csv"),
                        help="CSV containing numeric feature columns and a score column")
    parser.add_argument("--output", default=str(service_root / "models" / "random_forest.joblib"))
    parser.add_argument("--version", default=None)
    parser.add_argument("--random-state", type=int, default=42)
    args = parser.parse_args()

    dataset = pd.read_csv(args.data)
    if "score" not in dataset.columns:
        raise ValueError("Training CSV must contain a score column")
    feature_names = [column for column in dataset.columns if column != "score"]
    if not feature_names:
        raise ValueError("Training CSV must contain at least one feature column")
    if dataset[feature_names + ["score"]].isnull().any().any():
        raise ValueError("Training CSV cannot contain missing feature or score values")
    X = dataset[feature_names].astype(float)
    y = dataset["score"].astype(float)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=args.random_state)
    model = RandomForestRegressor(n_estimators=200, random_state=args.random_state, n_jobs=-1)
    model.fit(X_train, y_train)
    grade_accuracy = float(accuracy_score(y_test.map(grade), pd.Series(model.predict(X_test)).map(grade)))
    version = args.version or datetime.now(timezone.utc).strftime("rf-%Y%m%d%H%M%S")
    artifact = {"model": model, "feature_names": feature_names, "model_name": "food-quality-random-forest",
                "version": version, "algorithm": "RandomForestRegressor", "accuracy": grade_accuracy}
    output = Path(args.output)
    output.parent.mkdir(parents=True, exist_ok=True)
    joblib.dump(artifact, output)
    print("model=" + str(output) + " version=" + version + " grade_accuracy=" + str(grade_accuracy))


if __name__ == "__main__":
    main()
