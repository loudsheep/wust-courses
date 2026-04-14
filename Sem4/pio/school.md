```plantuml

class Uczelnia {
    nazwa: String
}

abstract class Osoba {
    imie: String
    nazwisko: String
    data_urodzenia: Date
    /pelne_imie: String
}

class Student {
    indeks: String {id}
    obliczSrednia(): Float
    oceny: Ocena[0..*] = {}
    historia_kursow: Kurs[0..*] {ordered, nonunique}
    zainteresowania: String[0..*]
}

class Prowadzacy {
    przypiszDoKursu(k: Kurs)
}

abstract class Pracownik {
    numer_pracownika: String {id}
    data_zatrudnienia: Date = dzis()
}

class Wydzial {
    kod: String {id}
    nazwa: String
}

class Kurs {
    kod: String {id}
    nazwa: String
    punkty: Integer
}

class Przedmiot {
    kod: String {id}
    nazwa: String
}

enum Ocena {
    NIEDOSTATECZNY
    DOSTATECZNY
    DOSTATECZNY_PLUS
    DOBRY
    DOBRY_PLUS
    BARDZO_DOBRY
}

class Zajecia {
    data: Date    
    czas: Time
}

class Sala {
    numer: String {id}
    pojemnosc: Integer
}

class Obecnosc {
    data: Date = dzis()
    status: StatusObecnosci = OBECNY
}

enum StatusObecnosci {
    OBECNY
    NIEOBECNY
    USPRAWIEDLIWIONY
}

' --- KLASA ASOCJACJI ---
class Zapis {
    data_zapisu: Date = dzis()
    status: String
}

' --- DZIEDZICZENIE ---
Osoba <|-- Student
Osoba <|-- Pracownik
Pracownik <|-- Prowadzacy

' --- AGREGACJA ---
Uczelnia "1" o-- "*" Wydzial
Wydzial "1" o-- "*" Kurs
Wydzial "1" o-- "*" Prowadzacy
Wydzial "1" o-- "*" Sala

' --- RELACJE ---
Prowadzacy "*" --> "*" Kurs : prowadzi
Kurs "*" --> "1" Przedmiot
Kurs "1" --> "*" Zajecia : okresla
Sala "1" --> "*" Zajecia : zajeta przez

' --- KLASA ASOCJACJI (ZAPIS) ---
Student "*" -- "*" Kurs : zapis
(Student, Kurs) .. Zapis

' --- KOMPOZYCJA ---
Zajecia "1" *-- "*" Obecnosc

' --- POZOSTAŁE ---
Obecnosc "*" --> "1" Student

```
school.md