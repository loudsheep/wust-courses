#!/usr/bin/env python3

import utils
from fuzzy_trimf_system_definition import depression_sim as fuzzy_sim

from sklearn.metrics import accuracy_score, f1_score, classification_report


def predict_fuzzy(X):
    results = []
    for _, row in X.iterrows():
        fuzzy_sim.reset()

        fuzzy_sim.input["social_media_while_eating"] = row["SocialMedia_WhileEating"]
        fuzzy_sim.input["overeating_level"] = row["Your overeating level"]
        # fuzzy_sim.input["employment_status"] = row["Employment_Status"]
        fuzzy_sim.input["low_energy"] = row["Low_Energy"]

        try:
            fuzzy_sim.compute()
            result = fuzzy_sim.output["depression_type"]
        except KeyError:
            result = 4.0

        results.append(round(result))

    return results


def main():
    DATA_PATH = "data/raw/Mental Health Classification.csv"
    TARGET_COL = "Depression_Type"

    _, X_test, _, y_test = utils.load_and_split_stripped_data(
        DATA_PATH, TARGET_COL, test_size=0.2
    )

    y_pred = predict_fuzzy(X_test)

    acc = accuracy_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred, average="weighted")
    report = classification_report(y_test, y_pred)

    print("\n=== FUZZY C-MEANS RESULTS ===")
    print(f"Accuracy: {acc:.4f}")
    print(f"F1 Score: {f1:.4f}")
    print("\nClassification Report:\n", report)


if __name__ == "__main__":
    main()
