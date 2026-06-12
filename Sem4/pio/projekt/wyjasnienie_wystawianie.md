# Logika diagramu klas — Wystawianie i eksport ocen

## Skąd wzięły się poszczególne elementy

Diagram powstał z trzech źródeł: przypadków użycia (co system ma robić),
diagramu aktywności (jak to robi krok po kroku) i istniejącego diagramu klas
(jakie klasy już istnieją i jak wyglądają konwencje).

---

## Encje dziedzinowe

### `OcenaKoncowa`

Centralna klasa całego diagramu. Reprezentuje fakt wystawienia oceny —
jest produktem końcowym przypadku użycia „Wpisanie oceny końcowej".

Atrybuty wynikają wprost z przypadku użycia:
- `wartosc: Ocena` — ocena musi być z dopuszczalnej skali (stąd osobny enum)
- `dataNadania` — system rejestruje kiedy ocena została wystawiona
- `id` — klucz, spójnie z konwencją istniejącego diagramu (`{id}`)

Metoda `getWartosc()` jest publiczna, bo inne klasy (np. `NotificationService`,
`ExportService`) muszą odczytać wartość oceny bez modyfikowania obiektu.

### `Ocena` (enum)

Pochodzi z istniejącego diagramu klas — był tam już zdefiniowany.
Celowo nie duplikujemy go jako osobnej klasy, tylko referencjonujemy ten sam enum.
Gwarantuje to, że walidacja w `GradeValidator` i zapis w `OcenaKoncowa`
operują na tym samym zestawie wartości.

### `EksportOcen`

Reprezentuje pojedynczą operację eksportu — log zdarzenia.
Przypadek użycia mówi wprost: „system rejestruje operację eksportu w logach".
To nie jest plik sam w sobie, tylko zapis że eksport się odbył, kiedy i w jakim formacie.

- `format` i `zakresOcen` — użytkownik wybiera je przed eksportem (krok 6 przebiegu podstawowego)
- `sciezkaPliku` — gdzie plik został zapisany lub skąd można go pobrać
- `generujPlik()` — deleguje właściwą robotę do `ExportService`, ale metoda
  jest tu, bo to `EksportOcen` jest odpowiedzialny za swój własny wynik

### `FormatEksportu` (enum)

Pochodzi z warunków wstępnych przypadku użycia eksportu:
„system posiada skonfigurowany format eksportu (np. PDF, CSV, XLSX)".
Enum zamiast zwykłego `String` — żeby system nie pozwolił wpisać np. `"docx"`.

### `GrupaZajeciowa`

Nie było jej w istniejącym diagramie, ale oba przypadki użycia na niej operują:
prowadzący wybiera grupę, eksport dotyczy konkretnej grupy.
Dlatego została dodana jako nowa klasa z minimalnym zestawem atrybutów.

---

## Serwisy (`<<control>>`)

Stereotyp `<<control>>` pochodzi bezpośrednio z diagramu aktywności —
każdy swimlane oznaczony `<<control>>` stał się osobną klasą serwisową.
Jest to warstwa logiki aplikacji, oddzielona od encji dziedzinowych.

### `GradeController`

Punkt wejścia dla obu przypadków użycia. Przyjmuje żądanie od użytkownika
(prowadzącego lub dziekanatu) i orkiestruje pozostałe serwisy.
Ma dwie metody — po jednej na przypadek użycia.

### `AuthorizationService`

Wynika z pierwszego warunku na diagramie aktywności:
„Czy posiada uprawnienia?". Bez pozytywnego wyniku tej weryfikacji
żaden z pozostałych kroków się nie wykonuje.
Dwie metody, bo uprawnienia do edycji ocen (prowadzący) i do eksportu
(dziekanat, administrator) są różne.

### `GradeValidator`

Wynika z drugiego warunku na diagramie aktywności:
„Czy ocena poprawna?". Odpowiada za przebieg alternatywny A w przypadku
użycia — próba wpisania oceny spoza skali.
Osobna klasa (nie metoda w kontrolerze), bo walidacja to osobna odpowiedzialność.

### `FinalGradeService`

Odpowiada za faktyczne utworzenie obiektu `OcenaKoncowa` i zapisanie go.
Na diagramie aktywności to akcja `createFinalGrade()` i `Utworzenie instancji obiektu oceny`.
Koordynuje też wysłanie powiadomienia — zleca to `NotificationService`.

### `GradeRepository`

Warstwa dostępu do bazy danych. Oddziela logikę biznesową od sposobu
przechowywania danych — `FinalGradeService` nie wie jak działa baza,
tylko mówi `save()`. Metoda `findByKursAndGrupa()` jest potrzebna przy eksporcie.

### `NotificationService`

Pochodzi z ostatniego kroku diagramu aktywności: `sendGradeNotification` z
`<<SendSignalAction>>`. Przypadek użycia mówi: „student otrzymuje powiadomienie
o wpisaniu oceny". To odrębny serwis, bo sposób wysyłania powiadomień
(email, push, sms) może się zmieniać niezależnie od logiki ocen.

### `ExportService`

Obsługuje przebieg podstawowy przypadku użycia eksportu (kroki 7–9).
Pobiera oceny przez `GradeRepository`, generuje plik i zapisuje log w `EksportOcen`.
Obsługuje też przebieg alternatywny B (błąd generowania) — wyjątek wyrzucony
przez `generateFile()` jest łapany przez `GradeController`.

---

## Relacje

### `OcenaKoncowa` → `Student`, `Prowadzacy`, `Kurs`

Asocjacje `0..*` do `1` — jedna ocena dotyczy dokładnie jednego studenta,
jednego prowadzącego i jednego kursu. Student może mieć wiele ocen końcowych
(z różnych kursów), prowadzący może wystawić wiele ocen.

Klasy `Student`, `Prowadzacy`, `Kurs` są tu bez atrybutów i metod,
bo ich pełne definicje są w istniejącym diagramie klas —
pokazujemy tylko że relacja istnieje.

### `Kurs` ◇── `GrupaZajeciowa` (agregacja)

Agregacja, nie kompozycja — grupa zajęciowa może teoretycznie istnieć
bez przypisanego kursu (np. w trakcie planowania). Spójność z istniejącym
diagramem, gdzie `Uczelnia` i `Wydzial` też używają agregacji (`o--`).

### Relacje między serwisami (→)

Zwykłe asocjacje kierunkowe — pokazują przepływ wywołań.
`GradeController` wie o wszystkich serwisach, ale serwisy niższego poziomu
(np. `GradeRepository`) nie wiedzą o kontrolerze.
Kierunek strzałki = kto kogo wywołuje.

---

## Czego celowo nie ma

**Metod `get`/`set` dla każdego atrybutu** — zaśmiecałyby diagram.
W PlantUML z `hide empty members` i tak byłyby ukryte jeśli puste,
a w praktyce wynikają ze standardu języka.

**Klasy `Użytkownik` jako rodzica** — prowadzący i dziekanat dziedziczą
z `Pracownik` → `Osoba` w istniejącym diagramie. Nie duplikujemy tej hierarchii.

**Klasy wyjątków** (`InvalidGradeException`) — na diagramie aktywności
jest `<<RaiseExceptionAction>>`, ale wyjątki zazwyczaj nie trafiają do
diagramu klas na poziomie akademickim, chyba że prowadzący tego wymaga.