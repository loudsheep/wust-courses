import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier, HistGradientBoostingClassifier

from plots import (
    test_decision_tree_depth,
    test_hist_gradient_boosting,
    test_logistic_regression,
    test_random_forest_depth,
    test_random_forest_depth_and_estimators,
    test_random_forest_estimators,
    test_random_forest_depth_and_estimators_surface,
)
from utils import (
    load_and_split_data,
    load_and_split_stripped_data,
    train_model,
    evaluate_model,
    save_model,
)


def run_all_features(data_path, target_col):
    print("\n=== Running tests: ALL FEATURES ===")
    X_train, X_test, y_train, y_test = load_and_split_data(data_path, target_col)
    suffix = "_all_features"

    test_random_forest_estimators(X_train, y_train, X_test, y_test, suffix=suffix)
    test_logistic_regression(X_train, y_train, X_test, y_test, suffix=suffix)
    test_decision_tree_depth(X_train, y_train, X_test, y_test, suffix=suffix)
    test_hist_gradient_boosting(X_train, y_train, X_test, y_test, suffix=suffix)
    test_random_forest_depth(X_train, y_train, X_test, y_test, suffix=suffix)
    test_random_forest_depth_and_estimators(X_train, y_train, X_test, y_test, suffix=suffix)
    test_random_forest_depth_and_estimators_surface(X_train, y_train, X_test, y_test, suffix=suffix)
    print("All-features tests done.")


def run_4_features(data_path, target_col):
    print("\n=== Running tests: 4 FEATURES (constrained) ===")
    X_train, X_test, y_train, y_test = load_and_split_stripped_data(data_path, target_col)
    suffix = "_4_features"

    rf_est = test_random_forest_estimators(X_train, y_train, X_test, y_test, suffix=suffix)
    lr_c   = test_logistic_regression(X_train, y_train, X_test, y_test, suffix=suffix)
    dt_dep = test_decision_tree_depth(X_train, y_train, X_test, y_test, suffix=suffix)
    hgb_lr = test_hist_gradient_boosting(X_train, y_train, X_test, y_test, suffix=suffix)
    rf_dep = test_random_forest_depth(X_train, y_train, X_test, y_test, suffix=suffix)
    test_random_forest_depth_and_estimators(X_train, y_train, X_test, y_test, suffix=suffix)
    test_random_forest_depth_and_estimators_surface(X_train, y_train, X_test, y_test, suffix=suffix)

    print("4-features tests done.")
    print("\n--- RF Estimators (4 features) ---")
    print(rf_est.to_string(index=False))
    print("\n--- RF Max Depth (4 features) ---")
    print(rf_dep.to_string(index=False))
    print("\n--- Decision Tree Depth (4 features) ---")
    print(dt_dep.to_string(index=False))
    print("\n--- Logistic Regression C (4 features) ---")
    print(lr_c.to_string(index=False))
    print("\n--- HistGradientBoosting LR (4 features) ---")
    print(hgb_lr.to_string(index=False))


def main():
    DATA_PATH = "data/raw/Mental Health Classification.csv"
    TARGET_COL = "Depression_Type"

    run_all_features(DATA_PATH, TARGET_COL)
    run_4_features(DATA_PATH, TARGET_COL)

    # --- Basic model comparison on stripped (4-feature) data ---
    X_train, X_test, y_train, y_test = load_and_split_stripped_data(
        DATA_PATH, TARGET_COL
    )

    models = {
        "Logistic Regression": LogisticRegression(random_state=42, max_iter=1000),
        "Decision Tree": DecisionTreeClassifier(random_state=42),
        "Random Forest": RandomForestClassifier(random_state=42, n_estimators=100),
        "HistGradientBoosting": HistGradientBoostingClassifier(random_state=42),
    }

    results = []

    for name, model in models.items():
        print(f"Training {name}...")
        trained_model = train_model(model, X_train, y_train)

        metrics = evaluate_model(trained_model, X_test, y_test)

        results.append(
            {
                "Model": name,
                "Accuracy": metrics["accuracy"],
                "F1 Score": metrics["f1_score"],
                "Classification Report": metrics["classification_report"],
            }
        )

        save_model(trained_model, f"models/{name.replace(' ', '_').lower()}.pkl")

    results_df = pd.DataFrame(results).sort_values(by="F1 Score", ascending=False)
    print("\nModel Performance (4 features):")
    print(results_df[["Model", "Accuracy", "F1 Score"]])

    rf_model = RandomForestClassifier(random_state=42, n_estimators=100)
    rf_model.fit(X_train, y_train)
    importances = pd.DataFrame(
        {"Cecha": X_train.columns, "Ważność": rf_model.feature_importances_}
    ).sort_values(by="Ważność", ascending=False)

    print("\n--- Ważność cech (Feature Importances) ---")
    print(importances)


if __name__ == "__main__":
    main()
