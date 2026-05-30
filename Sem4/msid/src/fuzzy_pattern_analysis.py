#!/usr/bin/env python3

import numpy as np
import pandas as pd
from itertools import product
from collections import Counter
import utils
from sklearn.metrics import accuracy_score, f1_score, classification_report
from sklearn.model_selection import train_test_split


def analyze_data_patterns(X, y):
    print("\n=== ANALIZA WZORCÓW DANYCH ===")

    # Połącz X i y dla łatwiejszej analizy
    df = X.copy()
    df["True_value"] = y

    # 1. Podstawowe statystyki
    print("\n1. Rozkład True_value:")
    value_counts = df["True_value"].value_counts().sort_index()
    for val, count in value_counts.items():
        print(f"   True_value {val}: {count} przypadków")

    # 2. Analiza kombinacji wejść dla każdego wyjścia
    print("\n2. Najczęstsze kombinacje dla każdego True_value:")

    # Definiujemy przedziały dla każdej zmiennej
    social_bins = [0, 1, 2, 3, 4]  # 0-3
    overeating_bins = [0, 3, 6, 9, 12]  # 0-12
    employment_values = [0, 1, 2, 3, 4]  # 0-4
    energy_values = [0, 1, 2]  # 0-2

    for target_val in sorted(df["True_value"].unique()):
        print(f"\n   True_value = {target_val}:")
        subset = df[df["True_value"] == target_val]

        # Najczęstsze wartości dla każdej zmiennej
        for col in [
            "SocialMedia_WhileEating",
            "Your overeating level",
            "Employment_Status",
            "Low_Energy",
        ]:
            mode_val = subset[col].mode()
            if not mode_val.empty:
                print(
                    f"     {col}: najczęściej {mode_val.values[0]:.1f} "
                    f"(średnia: {subset[col].mean():.2f})"
                )

    # 3. Sugestie reguł na podstawie częstych kombinacji
    print("\n3. Sugerowane reguły na podstawie częstych wzorców:")
    suggest_rules_from_data(df)

    return df


def suggest_rules_from_data(df):
    def discretize_social(value):
        if value <= 0.5:
            return "never"
        elif value <= 1.5:
            return "rarely"
        elif value <= 2.5:
            return "often"
        else:
            return "always"

    def discretize_overeating(value):
        if value <= 1:
            return "none"
        elif value <= 4:
            return "mild"
        elif value <= 7:
            return "moderate"
        else:
            return "severe"

    def discretize_employment(value):
        if value <= 0.4:
            return "unemployed"
        elif value <= 1.4:
            return "student"
        elif value <= 2.4:
            return "employed"
        elif value <= 3.4:
            return "self-employed"
        else:
            return "other"

    def discretize_energy(value):
        if value <= 0.4:
            return "no"
        elif value <= 1.4:
            return "yes"
        else:
            return "sometimes"

    def discretize_output(value):
        if value <= 2:
            return "result_1_2"
        elif value <= 4:
            return "result_3_4"
        elif value <= 5:
            return "result_5"
        elif value <= 7:
            return "result_6_7"
        elif value <= 9:
            return "result_8_9"
        else:
            return "result_10_11"

    # Dyskretyzuj dane
    df_disc = df.copy()
    df_disc["Social_disc"] = df["SocialMedia_WhileEating"].apply(discretize_social)
    df_disc["Overeating_disc"] = df["Your overeating level"].apply(
        discretize_overeating
    )
    df_disc["Employment_disc"] = df["Employment_Status"].apply(discretize_employment)
    df_disc["Energy_disc"] = df["Low_Energy"].apply(discretize_energy)
    df_disc["Output_disc"] = df["True_value"].apply(discretize_output)

    # Znajdź najczęstsze kombinacje
    print("\n   Najczęstsze pełne kombinacje:")
    combinations = (
        df_disc.groupby(
            [
                "Social_disc",
                "Overeating_disc",
                "Employment_disc",
                "Energy_disc",
                "Output_disc",
            ]
        )
        .size()
        .sort_values(ascending=False)
        .head(20)
    )

    for (social, overeating, employment, energy, output), count in combinations.items():
        print(
            f"   IF social={social} AND overeating={overeating} AND "
            f"employment={employment} AND energy={energy} THEN {output} "
            f"({count} przypadków)"
        )

    # Znajdź uproszczone reguły (pomijając niektóre zmienne)
    print("\n   Uproszczone reguły (top 2 zmienne + wyjście):")

    # Analiza par zmiennych
    for var1, var2 in [
        ("Social_disc", "Overeating_disc"),
        ("Overeating_disc", "Energy_disc"),
        ("Social_disc", "Energy_disc"),
    ]:
        combinations_simple = (
            df_disc.groupby([var1, var2, "Output_disc"])
            .size()
            .sort_values(ascending=False)
            .head(10)
        )

        for (v1, v2, output), count in combinations_simple.items():
            print(
                f"   IF {var1.split('_')[0]}={v1} AND "
                f"{var2.split('_')[0]}={v2} THEN {output} "
                f"({count} przypadków)"
            )


def evaluate_rules_quality(X, y, fuzzy_sim):
    """
    Ewaluuje jakość obecnych reguł i pokazuje, które przypadki są źle klasyfikowane
    """
    print("\n=== EWALUACJA JAKOŚCI REGUŁ ===")

    results = []
    errors = []

    for idx, row in X.iterrows():
        fuzzy_sim.reset()

        fuzzy_sim.input["social_media_while_eating"] = row["SocialMedia_WhileEating"]
        fuzzy_sim.input["overeating_level"] = row["Your overeating level"]
        fuzzy_sim.input["employment_status"] = row["Employment_Status"]
        fuzzy_sim.input["low_energy"] = row["Low_Energy"]

        try:
            fuzzy_sim.compute()
            result = fuzzy_sim.output["depression_type"]
        except KeyError:
            result = 5.0

        pred = round(result)
        true_val = y[idx]
        results.append(pred)

        if pred != true_val:
            errors.append(
                {
                    "index": idx,
                    "social": row["SocialMedia_WhileEating"],
                    "overeating": row["Your overeating level"],
                    "employment": row["Employment_Status"],
                    "energy": row["Low_Energy"],
                    "predicted": pred,
                    "true": true_val,
                    "diff": abs(pred - true_val),
                }
            )

    # Sortuj błędy według wielkości różnicy
    errors.sort(key=lambda x: x["diff"], reverse=True)

    print(f"\nLiczba błędów: {len(errors)} na {len(X)} przypadków")
    print(f"Dokładność: {accuracy_score(y, results):.4f}")

    if errors:
        print("\nNajwiększe błędy (różnica >= 3):")
        big_errors = [e for e in errors if e["diff"] >= 3]
        for err in big_errors[:10]:
            print(
                f"  Indeks {err['index']}: social={err['social']}, "
                f"overeating={err['overeating']}, emp={err['employment']}, "
                f"energy={err['energy']} -> predicted={err['predicted']}, "
                f"true={err['true']} (różnica: {err['diff']})"
            )

    return results, errors


def generate_rule_suggestions(errors):
    """
    Generuje konkretne sugestie reguł na podstawie błędów
    """
    print("\n=== SUGESTIE POPRAWEK REGUŁ ===")

    # Grupuj błędy według podobnych wzorców
    error_patterns = {}
    for err in errors:
        key = (
            round(err["social"]),
            round(err["overeating"] / 3) * 3,  # Grupuj co 3
            round(err["employment"]),
            round(err["energy"]),
        )
        if key not in error_patterns:
            error_patterns[key] = []
        error_patterns[key].append(err)

    for pattern, errs in sorted(
        error_patterns.items(), key=lambda x: len(x[1]), reverse=True
    )[:10]:
        if len(errs) >= 3:  # Tylko jeśli wzorzec się powtarza
            true_vals = [e["true"] for e in errs]
            most_common_true = Counter(true_vals).most_common(1)[0][0]

            print(
                f"\nWzorzec: social≈{pattern[0]}, overeating≈{pattern[1]}, "
                f"emp≈{pattern[2]}, energy≈{pattern[3]}"
            )
            print(f"  Liczba błędów: {len(errs)}")
            print(f"  Najczęstsza prawdziwa wartość: {most_common_true}")
            print(
                f"  Sugestia: Dodaj regułę dla tego wzorca z wyjściem {most_common_true}"
            )


def cross_validate_rules(X, y, fuzzy_sim, n_splits=5):
    """
    Walidacja krzyżowa reguł
    """
    print(f"\n=== WALIDACJA KRZYŻOWA ({n_splits}-krotna) ===")

    from sklearn.model_selection import KFold

    kf = KFold(n_splits=n_splits, shuffle=True, random_state=42)

    accuracies = []
    f1_scores = []

    for fold, (train_idx, test_idx) in enumerate(kf.split(X)):
        X_test = X.iloc[test_idx]
        y_test = y.iloc[test_idx]

        results = []
        for idx, row in X_test.iterrows():
            fuzzy_sim.reset()
            fuzzy_sim.input["social_media_while_eating"] = row[
                "SocialMedia_WhileEating"
            ]
            fuzzy_sim.input["overeating_level"] = row["Your overeating level"]
            fuzzy_sim.input["employment_status"] = row["Employment_Status"]
            fuzzy_sim.input["low_energy"] = row["Low_Energy"]

            try:
                fuzzy_sim.compute()
                result = fuzzy_sim.output["depression_type"]
            except KeyError:
                result = 5.0

            results.append(round(result))

        acc = accuracy_score(y_test, results)
        f1 = f1_score(y_test, results, average="weighted")
        accuracies.append(acc)
        f1_scores.append(f1)

        print(f"Fold {fold + 1}: Accuracy={acc:.4f}, F1={f1:.4f}")

    print(
        f"\nŚrednia Accuracy: {np.mean(accuracies):.4f} (+/- {np.std(accuracies):.4f})"
    )
    print(f"Średnia F1: {np.mean(f1_scores):.4f} (+/- {np.std(f1_scores):.4f})")


def main():
    DATA_PATH = "data/raw/Mental Health Classification.csv"
    TARGET_COL = "Depression_Type"

    # Wczytaj dane
    X, _, y, _ = utils.load_and_split_data(DATA_PATH, TARGET_COL)

    # 1. Analiza danych
    df_analyzed = analyze_data_patterns(X, y)

    # 5. Test na różnych podzbiorach
    print("\n=== TEST NA PODZBIORACH DANYCH ===")


if __name__ == "__main__":
    main()
