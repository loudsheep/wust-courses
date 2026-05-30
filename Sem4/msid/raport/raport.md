# Porównanie systemów logiki rozmytej i uczenia maszynowego w klasyfikacji zdrowia psychicznego

**Autorzy:** Kinga Adamska 284236, Roch Mykietów 284240

---

## 1. Cel i charakter problemu

### Definicja problemu

Problem badawczy polega na przewidywaniu i klasyfikacji typu depresji (`Depression_Type`)
na podstawie danych zebranych w zbiorze "Mental Health Classification.csv".
Głównym celem systemu jest porównanie logiki rozmytej i uczenia maszynowego w klasyfikacji zdrowia psychicznego.

Aby zapewnić rzetelność oceny, przed przystąpieniem do uczenia wykluczono atrybuty mogące powodować bezpośredni wyciek informacji o zmiennej docelowej (tzw. _data leakage_), takie jak `Depression_Score`, `Symptoms`, `Nervous_Level` czy `Coping_Methods`.

TODO: co to za metoda
W dalszej analizie metodą ... wybrano 4 wartości: `Your overeating level`, `SocialMedia_While_eating`, `Employement_status`, `Low_Energy` pozwalające na kompromis między dokładnością a złożonością analizy danych.

### Złożoność problemu i metodyka

Z uwagi na złożoność i wielowymiarowość danych medycznych oraz psychologicznych, relacje między cechami a zmienną wynikową mają charakter silnie nieliniowy, co znacząco utrudnia użycie fuzzy logic.

TODO: **tu wpadną wykresy z rozłożeniem depression_type oraz innych parametrów w danych wejsciowych, żeby wyjaśnić dlaczego porównawczymi metodami jest acuraccy i F1 score, warto też wspomnieć imo o prędkości działania gotowego modelu**

---

## 2. Stosowane sposoby rozwiązania problemu

### 2.1. Stosowane modele uczenia maszynowego

Dla oceny możliwości rozpoznawania wzorców użyto i porównano ze sobą 4 popularne modele uczenia maszynowego:

1. **Regresja logistyczna (Logistic Regression):**
   Podstawowy, liniowy model klasyfikacyjny. Działa poprzez oszacowanie prawdopodobieństwa przynależności do danej klasy na podstawie liniowej kombinacji cech wejściowych, a następnie przepuszczenie wyniku przez funkcję sigmoidalną.
2. **Drzewo decyzyjne (Decision Tree):**
   Algorytm dokonujący hierarchicznego, binarnego podziału przestrzeni cech wejściowych aż do osiągnięcia jednorodnych klas w tzw. liściach. Model cechuje się wysoką interpretowalnością, ale jest podatny na przeuczenie.
3. **Las losowy (Random Forest):**
   Metoda zespołowa (ang. _ensemble method_) łącząca wyniki wielu niezależnych drzew decyzyjnych (tzw. zjawisko "mądrości tłumu"). Dzięki uśrednianiu, wariant ten znacząco redukuje ryzyko przeuczenia i wariancję, zachowując przy tym wysoką moc predykcyjną.
4. **Gradient Boosting oparty o histogramy (HistGradientBoosting):**
   Algorytm sekwencyjny budujący kolejne drzewa tak, by korygować błędy popełniane przez swoich poprzedników. Optymalizacja histogramowa pozwala znacząco przyspieszyć proces tworzenia wezłów i podziału zbiorów dla dużych wolumenów danych.

### 2.2. Stosowane techniki fuzzy logic

**to będzie opisane bardziej profesjonalnie**

1. Basic Fuzzy - wykorzystuje ręcznie napisane reguły na podstawie wcześniejszej analizy danych
2. Data Driven Fuzzy - wykorzystuje clustering **Fuzzy C-Means**
3. Data Driven Pure Fuzzy - wykorzystuje clustering **Fuzzy C-Means** rozszerzając poprzedni system o filtr jakości klastra.
   TODO: Pure fuzzy to chujowa nazwa

---

## 3. Testy parametrów algorytmów uczenia maszynowego na pełnym zbiorze danych

Dla wszystkich przetestowanych modeli wykonano eksperymenty dostrajania hiperparametrów metodą optymalizacji wrażliwości w celu znalezienia kompromisu dla jak najwyższej precyzji w odniesieniu do metryk **Accuracy** (Dokładność) oraz **F1 Score**.

### 3.1 Random Forest: Liczba estymatorów (`n_estimators`)

Badanie wpływu liczby wykorzystanych drzew w zespole na jakość końcowej klasyfikacji.

![Random Forest - Estimators](../plots/rf_estimators_all_features.png)

| Liczba estymatorów | Accuracy | F1 Score |
| :----------------- | :------- | :------- |
| 10                 | 0.9800   | 0.9546   |
| 25                 | 0.9825   | 0.9519   |
| 50                 | 0.9825   | 0.9519   |
| 100                | 0.9850   | 0.9615   |
| 200                | 0.9850   | 0.9615   |
| 500                | 0.9850   | 0.9615   |

**Analiza:**
Skuteczność klasyfikacji ulega znacznej poprawie na początku wzrostu wielkości lasu i osiąga swój optymalny pułap około 100 estymatorów (Accuracy 98.5%). Zwiększanie rozmiaru lasu powyżej 100 drzew decyzyjnych marnuje jedynie czas i zasoby obliczeniowe nie wnosząc żadnej poprawy do wskaźników jakości klasyfikacji.

### 3.2 Random Forest: Maksymalna głębokość (`max_depth`)

Analiza zachowania złożoności modelu determinowanej przez maksymalną głębokość drzew składowych.

![Random Forest - Max Depth](../plots/rf_max_depth_all_features.png)

| Maksymalna głębokość | Accuracy | F1 Score |
| :------------------- | :------- | :------- |
| 5                    | 0.7125   | 0.6453   |
| 10                   | 0.9800   | 0.9408   |
| 20                   | 0.9850   | 0.9615   |
| 50                   | 0.9850   | 0.9615   |
| 100                  | 0.9850   | 0.9615   |

**Analiza:**
Płytkie drzewa (głębokość 5) są niedouczone (ang. _underfitting_) i osiągają relatywnie niskie rezultaty (Accuracy: 71.2%). Dopiero zagłębienie drzew od ok. 20 poziomów zapewnia najwyższą, stabilną jakość wyników (98.5%).

### 3.3 Decision Tree: Maksymalna głębokość (`max_depth`)

Taki sam parametr jak powyżej przetestowano dla pojedynczego drzewa decyzyjnego.

![Decision Tree - Max Depth](../plots/decision_tree_depth_all_features.png)

| Maksymalna głębokość | Accuracy | F1 Score |
| :------------------- | :------- | :------- |
| 5                    | 0.5525   | 0.5637   |
| 10                   | 0.8700   | 0.7920   |
| 20                   | 0.9850   | 0.9615   |
| 50                   | 0.9850   | 0.9615   |
| 100                  | 0.9850   | 0.9615   |

**Analiza:**
Pojedyncze drzewo jest jeszcze mocniej czułe na zbyt duże ograniczenie swojej struktury (głębokość 5 to zaledwie Accuracy 55.25%). Pozwolenie drzewu na optymalny rozwój struktury decyzyjnej do 20 poziomów zrównuje jego wynik z modelem Random Forest (Accuracy: 98.5%).

### 3.4 Logistic Regression: Odwrotność siły regularyzacji (`C`)

Wpływ hiperparametru regularyzacji (przeciwdziałania przeuczeniu wektorów wag) na trafność modelu liniowego.

![Logistic Regression - C](../plots/logistic_regression_C.png)

| C (odwrotność siły) | Accuracy | F1 Score |
| :------------------ | :------- | :------- |
| 0.01                | 0.3950   | 0.1152   |
| 0.1                 | 0.5225   | 0.4921   |
| 1.0                 | 0.5900   | 0.7293   |
| 10.0                | 0.6250   | 0.8067   |
| 100.0               | 0.6225   | 0.8080   |

**Analiza:**
Regresja logistyczna radzi sobie bardzo słabo z postawionym wyzwaniem ze względu na swoje nieliniowe ograniczenia, osiągając maksymalnie zaledwie 62.5% dokładności klasyfikacji (dla wartości współczynnika C = 10.0, gdzie silniejsza regularyzacja dusi proces uczenia, a słabsza nie polepsza już dopasowania do wariancji).

### 3.5 HistGradientBoosting: Współczynnik uczenia (`learning_rate`)

Wrażliwość współczynnika optymalizacji procesu gradientowego w iteracyjnym budowaniu drzew.

![HistGradient Boosting - Learning Rate](../plots/hist_gradient_boosting_lr.png)

| Learning Rate | Accuracy | F1 Score |
| :------------ | :------- | :------- |
| 0.01          | 0.9800   | 0.9408   |
| 0.1           | 0.9750   | 0.9260   |
| 0.2           | 0.3250   | 0.2018   |
| 0.5           | 0.2625   | 0.1793   |
| 1.0           | 0.0875   | 0.0564   |
| 10.0          | 0.2650   | 0.0404   |

**Analiza:**
Kluczem do optymalnego gradient boostingu okazały się bardzo niskie wartości współczynnika uczenia. Optymalną wydajność osiągnięto przy bardzo wolnym uczeniu i dopasowywaniu wag `learning_rate` na poziomie zaledwie 0.01 (Accuracy 98%). Znaczne podniesienie progu nauki doprowadza szybko do "wystrzelenia" i utraty sterowności po funkcji błędu (drastyczny spadek dokładności już przy LR=0.2).

### 3.6

TODO: **Tu wpadną wykresy porównujące różne purity, sposoby wybierania reguł fuzzy logic i inne takie itp.**

## 4. Porównanie działania algorytmów ml (Optymalizacja przestrzeni wielowymiarowej)

Poniżej zamieszczono dodatkowy wykres wizualizujący zachowanie jakości modelu algorytmu Random Forest na podstawie manipulacji wielowymiarowej uwzględniając na raz głębokość w korelacji z ilością estymatorów.

![Random Forest - Depth vs Estimators](../plots/rf_depth_estimators_surface.png)

Przestrzeń poszukiwań potwierdza zjawisko stabilnego płaskowyżu. Najwyższa jakość gwarantowana jest dla rozwiązań odciętych z głębokością ponad 20 oraz wielkością lasu powyżej 100 składowych.

**tutaj chciałabym wrzucić jak radziły sobie modele o wyższych i niższych ilosciach danych wejsciowych uwzględniając czas pracy ale tez F1 (okazuje się że fuzzy logic wcale nie wypada tak źle na tle modeli, ponieważ liczy się nie tylko accuracy)**

---

## 5. Wnioski i podsumowanie

**wnioskami się jeszcze nie zajmowałam, bo fajnie najpierw zrobić wcześniejszą część raportu w pełni**
Zestawienie całościowe wskazuje bezsprzecznie na fakt wyższości skomplikowanych i złożonych struktur modeli opartych na zespołach nieliniowych modeli decyzyjnych do klasyfikacji problemu zdrowia psychicznego na podstawie atrybutów życiowych:

- **Przewaga opartych na drzewach modeli nad modelami liniowymi**: Algorytmy Random Forest, Decision Tree czy HistGradientBoosting operują na sprawności oscylującej wokół wskaźnika 98,5%. Dla porównania uwarunkowany liniowo model Logistic Regression ma wynik słabszy rzędu ~62.5%. To dowodzi ewidentnego i złożonego nieliniowego splotu atrybutów.
- **Rozsądek parametrów**: Wykresy badające wrażliwość na parametry dowiodły kluczowego zjawiska, według którego zbyt złożone algorytmy i duże wielkości parametrów wcale nie przynoszą korzyści z przeuczenia. Stabilność modeli typu Random Forest i Decision Tree stabilizuje się dla racjonalnych wielkości jak 100 drzew czy 20 węzłów podziałów w głębokości – zyskując zarówno na sprawności czasowej jak i klasyfikacji.
- **Rekomendacja końcowa:** Jako rozwiązanie bazowe dla przewidywania poziomu i typu załamania nerwowego czy zjawiska depresji rekomenduje się w pełni model zespołu Lasu Losowego (**Random Forest**). Oprócz posiadania uśrednionego kompromisu wysokiej jakości predykcji zabezpiecza on strukturę przed przeuczeniem o wiele efektywniej niż pojedyncze głębokie Drzewo Decyzyjne pomimo równego wyniku dla obydwu.
