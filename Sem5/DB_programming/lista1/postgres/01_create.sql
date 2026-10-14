-- Lista projekttowa 1 - skrypt tworzący schema w Postgres

CREATE TABLE Funkcje (
    funkcja    VARCHAR(10) CONSTRAINT funkcje_pk PRIMARY KEY,
    min_myszy  NUMERIC(3)  CONSTRAINT funkcje_min_ck CHECK (min_myszy > 5),
    max_myszy  NUMERIC(3)  CONSTRAINT funkcje_max_ck CHECK (max_myszy < 200),
    -- trzeba na poziomie tabeli - odnosimy się do dwuch atrybutów
    CONSTRAINT funkcje_minmax_ck CHECK (max_myszy >= min_myszy)
);

CREATE TABLE Wrogowie (
    imie_wroga       VARCHAR(15) CONSTRAINT wrogowie_pk PRIMARY KEY,
    stopien_wrogosci NUMERIC(2)  CONSTRAINT wrogowie_stopien_ck CHECK (stopien_wrogosci BETWEEN 1 AND 10),
    gatunek          VARCHAR(15),
    lapowka          VARCHAR(20)
);

CREATE TABLE Bandy (
    nr_bandy   NUMERIC(2)  CONSTRAINT bandy_pk PRIMARY KEY,
    nazwa      VARCHAR(20) CONSTRAINT bandy_nazwa_nn NOT NULL,
    teren      VARCHAR(15) CONSTRAINT bandy_teren_uq UNIQUE,
    szef_bandy VARCHAR(15) CONSTRAINT bandy_szef_uq UNIQUE
);

CREATE TABLE Kocury (
    imie            VARCHAR(15) CONSTRAINT kocury_imie_nn NOT NULL,
    plec            VARCHAR(1)  CONSTRAINT kocury_plec_ck CHECK (plec IN ('M', 'D')),
    pseudo          VARCHAR(15) CONSTRAINT kocury_pk PRIMARY KEY,
    funkcja         VARCHAR(10) CONSTRAINT kocury_funkcja_fk REFERENCES Funkcje (funkcja),
    szef            VARCHAR(15) CONSTRAINT kocury_szef_fk REFERENCES Kocury (pseudo),
    w_stadku_od     DATE        DEFAULT CURRENT_DATE,
    przydzial_myszy NUMERIC(3),
    myszy_extra     NUMERIC(3),
    nr_bandy        NUMERIC(2)  CONSTRAINT kocury_nr_bandy_fk REFERENCES Bandy (nr_bandy)
);

CREATE TABLE Wrogowie_kocurow (
    pseudo         VARCHAR(15) CONSTRAINT wk_pseudo_fk REFERENCES Kocury (pseudo),
    imie_wroga     VARCHAR(15) CONSTRAINT wk_imie_wroga_fk REFERENCES Wrogowie (imie_wroga),
    data_incydentu DATE        CONSTRAINT wk_data_nn NOT NULL,
    opis_incydentu VARCHAR(50),
    -- klucz zlożowny
    CONSTRAINT wk_pk PRIMARY KEY (pseudo, imie_wroga)
);

-- tutaj relacja cykliczna, więc trzeba stworzyc poniżej (dopuszczalny wyjątek)
ALTER TABLE Bandy
    ADD CONSTRAINT bandy_szef_fk FOREIGN KEY (szef_bandy) REFERENCES Kocury (pseudo);
