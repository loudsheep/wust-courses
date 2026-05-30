### Wytyczne ogólne dla raportu

- **Wizualizacja danych:** Raport musi obficie korzystać z wykresów liniowych (np. koszt w funkcji liczby iteracji) oraz przejrzystych tabel zestawiających kluczowe metryki.

- **Analiza:** Każda sekcja testowa musi kończyć się merytorycznym wnioskiem tłumaczącym zachowanie algorytmu i wpływ testowanego elementu na czas i jakość rozwiązania.

---

### Struktura Raportu

#### Nagłówek dokumentu

- **Tytuł:** Powinien jasno określać, jakie algorytmy są porównywane i na jakim problemie (np. "Porównanie efektywności algorytmów mrówkowych i symulowanego wyżarzania w rozwiązywaniu problemu QAP").

- **Autorzy:** Należy wypisać autorów wraz z przypisanymi do nich algorytmami lub częściami pracy.

#### 1. Charakterystyka problemu

W tej sekcji należy szczegółowo opisać problem optymalizacyjny, który rozwiązują algorytmy.

- **Definicja problemu:** Opisz, czym jest badany problem (np. Kwadratowe Zagadnienie Przydziału) i jaki jest jego główny cel (np. zminimalizowanie przepływu między lokalizacjami).

- **Sformalizowanie matematyczne:** Zdefiniuj funkcję celu oraz objaśnij wszystkie zmienne i macierze wchodzące w jej skład (np. macierz przepływów, macierz odległości). Do zapisu skomplikowanych wzorów matematycznych używaj odpowiedniej notacji formalnej.

- **Złożoność obliczeniowa:** Określ klasę problemu (np. problem NP-trudny) i uzasadnij, dlaczego wymaga on stosowania heurystyk (np. brak możliwości znalezienia optymalnego rozwiązania w akceptowalnym czasie przy pomocy przeglądu zupełnego).

#### 2. Stosowane metaheurystyki optymalizacyjne

Sekcja ta ma za zadanie teoretycznie wprowadzić czytelnika w wykorzystywane algorytmy.

- Dla każdego badanego algorytmu utwórz podsekcję (np. 2.1 Algorytm Mrówkowy, 2.2 Algorytm Symulowanego Wyżarzania).

- **Inspiracja i mechanika:** Opisz, zjawiskiem z jakiej dziedziny inspirowany jest algorytm (np. zachowanie mrówek poszukujących pożywienia, proces powolnego studzenia stopionych metali). Wyjaśnij zasady jego działania (np. budowanie śladu feromonowego, sterowanie prawdopodobieństwem akceptacji gorszych rozwiązań za pomocą temperatury).

- **Wzory i reguły:** Przedstaw kluczowe równania matematyczne sterujące algorytmem (np. reguła proporcjonalnej selekcji, parowanie feromonu, kryterium Metropolisa) wraz z opisem parametrów wejściowych.

#### 3 i 4. Testy parametrów algorytmów

Kolejne sekcje powinny być poświęcone testowaniu wrażliwości poszczególnych algorytmów na zmiany ich parametrów konfiguracyjnych.

- Utwórz odrębną podsekcję dla każdego kluczowego parametru (np. dla ACO: parametr alfa, parametr beta, rozmiar kolonii, itp.; dla SA: temperatura początkowa, współczynnik chłodzenia).

- **Elementy podsekcji testowej:**

1. **Wykres:** Zobrazowanie kosztu w funkcji iteracji dla różnych wartości badanego parametru na jednym wspólnym wykresie.

2. **Tabela wyników:** Musi zawierać kolumny: "Wartość parametru", "Koszt najlepszego rozwiązania" oraz "Czas wykonania (s)".

3. **Analiza:** Pisemne podsumowanie tabeli i wykresu. Należy wskazać optymalną wartość parametru, omówić relację (kompromis) między precyzją (kosztem) a szybkością obliczeń oraz skomentować widoczne trendy (np. "zbyt wysoka temperatura początkowa marnuje czas").

#### 5. Testy sprawności algorytmów dla poszczególnych problemów wejściowych

Ta część ocenia zachowanie poszczególnych algorytmów na różnorodnych zestawach danych (instancjach problemu). Należy podzielić ją na główne algorytmy (np. 5.1 ACO, 5.2 SA), a następnie na konkretne instancje.

- Zbadaj algorytmy na problemach o różnej wielkości i specyfice (np. problem mały n=7, problem duży n=30, instancja ze zwiększonym rozproszeniem punktów, instancja ze zróżnicowaną macierzą przepływów).

- Dla każdego scenariusza wykonaj kilka prób (np. 5 uruchomień), aby zbadać powtarzalność i stabilność algorytmu.

- Dostarcz **Wykres zbieżności** dla wszystkich prób oraz **Tabelę** (kolumny: Numer próby, Koszt najlepszego rozwiązania, Czas wykonania).

- Dokonaj analizy struktury wykresu (np. "struktura schodkowa") oraz rozbieżności wyników pomiędzy próbami, co wskazuje na wrażliwość na warunki początkowe.

#### 6. Porównanie działania algorytmów

W tej sekcji następuje bezpośrednie starcie obu algorytmów na tych samych zestawach testowych.

- Dla każdego wariantu problemu (mały, duży, specyficzny), zaprezentuj wykres nakładający na siebie krzywe zbieżności najlepszych konfiguracji badanych algorytmów.

- Załącz **Tabelę porównawczą** (kolumny: Algorytm, Koszt najlepszego rozwiązania, Czas wykonania).

- Wnioski w tej sekcji muszą jasno rozstrzygać, który algorytm był lepszy w danym scenariuszu i dlaczego (np. porównanie mechanizmu wędrowania w SA do pamięci zbiorowej mrówek w ACO). Wskazuj na różnice rzędów wielkości w czasie wykonania względem jakości.

#### 7. Wnioski

Dokument musi kończyć się ogólnym podsumowaniem.

- Zestaw ze sobą główne wady i zalety badanych podejść na podstawie całości zebranych danych.

- Sformułuj ostateczne rekomendacje - w jakich warunkach (np. gdy liczy się najwyższa jakość rozwiązania) należy wybrać jeden algorytm, a kiedy (np. gdy kluczowy jest czas ułamka sekundy) drugi algorytm sprawdzi się lepiej.

