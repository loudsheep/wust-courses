# Analiza Danych o Zdrowiu Psychicznym

Cel projektu: analiza danych dotyczących zdrowia psychicznego.
Rozpoznawanie symptomów i poziomu depresji zależnie od m.in.
poziomu wykształcenia, zatrudnienia lub czasu spędzanego na social mediach.

## Zbiory danych

[Aktualny zbiór danych - 2000 wierszy, 21 kolumn](https://data.mendeley.com/datasets/xppzm3kv9g/2)

[Wcześniej rozważany zbiór](https://www.kaggle.com/datasets/sehaj1104/student-mental-health-and-burnout-dataset/data)

## Metody

### 1. Machine Learning (scikit-learn)

W projekcie wykorzystano klasyczne modele uczenia maszynowego:

- Logistic Regression
- Decision Tree Classifier
- Random Forest Classifier
- HistGradientBoosting Classifier

Modele zostały porównane pod względem:

- Accuracy
- F1-score (macro)
- czasu trenowania
- czasu predykcji

### 2. System oparty o logikę rozmytą (Fuzzy Logic)

W projekcie zastosowano system decyzyjny oparty o logikę rozmytą z użyciem biblioteki scikit-fuzzy.

System zawiera:

- zmienne wejściowe (np. wiek, stres, sen, czas social media)
- funkcje przynależności (trimf, trapmf, gaussmf)
- zestaw reguł decyzyjnych IF-THEN
- defuzyfikację (wyznaczenie wyniku końcowego)

Badano wpływ:

- liczby cech wejściowych
- liczby funkcji przynależności
- typu funkcji przynależności

## Metryki oceny

W projekcie zastosowano następujące metryki:

- Accuracy – ogólna skuteczność modelu
- F1-score (macro) – równoważy precision i recall dla wszystkich klas
- Czas trenowania – wydajność modelu ML
- Czas predykcji – szybkość działania systemu decyzyjnego## Metryki oceny

W projekcie zastosowano następujące metryki:

- Accuracy – ogólna skuteczność modelu
- F1-score (macro) – równoważy precision i recall dla wszystkich klas
- Czas trenowania – wydajność modelu ML
- Czas predykcji – szybkość działania systemu decyzyjnego

## Eksperymenty dla logiki rozmytej

Przeprowadzono analizę wpływu parametrów systemu fuzzy:

### 1. Liczba cech wejściowych

Sprawdzono wpływ redukcji i zwiększania liczby atrybutów.

### 2. Liczba funkcji przynależności

Porównano modele z:

- 2 funkcjami (niski / wysoki)
- 3 funkcjami (niski / średni / wysoki)
- 5 funkcjami

### 3. Typ funkcji przynależności

Testowane funkcje:

- trimf (trójkątna)
- trapmf (trapezowa)
- gaussmf (gaussowska)

## Wyniki

Porównanie wyników modeli ML vs Fuzzy

| Model | Accuracy | F1 score | Train time [s] | Prediction time [ms] |
| ----- | -------- | -------- | -------------- | -------------------- |
|       |          |          |                |                      |
|       |          |          |                |                      |
|       |          |          |                |                      |

Wpływ liczby cech (Fuzzy)

| Liczba cech | Accuracy | F1 score |
| ----------- | -------- | -------- |
|             |          |          |
|             |          |          |
|             |          |          |

Typy funkcji przynależności

| Funkcja | Accuracy | F1 score |
| ------- | -------- | -------- |
|         |          |          |
|         |          |          |
|         |          |          |

Liczba funkcji przynależności

| Liczba funkcji | Accuracy | F1 score |
| -------------- | -------- | -------- |
|                |          |          |
|                |          |          |

## Wnioski
