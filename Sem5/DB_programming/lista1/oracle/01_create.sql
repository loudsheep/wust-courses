-- Lista projekttowa 1 - skrypt tworzący schema w Oracle

CREATE TABLE Funkcje (
    funkcja    VARCHAR2(10) CONSTRAINT funkcje_pk PRIMARY KEY,
    min_myszy  NUMBER(3)    CONSTRAINT funkcje_min_ck CHECK (min_myszy > 5),
    max_myszy  NUMBER(3)    CONSTRAINT funkcje_max_ck CHECK (max_myszy < 200),
    -- trzeba na poziomie tabeli - odnosimy się do dwuch atrybutów
    CONSTRAINT funkcje_minmax_ck CHECK (max_myszy >= min_myszy)
);

CREATE TABLE Wrogowie (
    imie_wroga       VARCHAR2(15) CONSTRAINT wrogowie_pk PRIMARY KEY,
    stopien_wrogosci NUMBER(2)    CONSTRAINT wrogowie_stopien_ck CHECK (stopien_wrogosci BETWEEN 1 AND 10),
    gatunek          VARCHAR2(15),
    lapowka          VARCHAR2(20)
);

CREATE TABLE Bandy (
    nr_bandy   NUMBER(2)    CONSTRAINT bandy_pk PRIMARY KEY,
    nazwa      VARCHAR2(20) CONSTRAINT bandy_nazwa_nn NOT NULL,
    teren      VARCHAR2(15) CONSTRAINT bandy_teren_uq UNIQUE,
    szef_bandy VARCHAR2(15) CONSTRAINT bandy_szef_uq UNIQUE
);

CREATE TABLE Kocury (
    imie            VARCHAR2(15) CONSTRAINT kocury_imie_nn NOT NULL,
    plec            VARCHAR2(1)  CONSTRAINT kocury_plec_ck CHECK (plec IN ('M', 'D')),
    pseudo          VARCHAR2(15) CONSTRAINT kocury_pk PRIMARY KEY,
    funkcja         VARCHAR2(10) CONSTRAINT kocury_funkcja_fk REFERENCES Funkcje (funkcja),
    szef            VARCHAR2(15) CONSTRAINT kocury_szef_fk REFERENCES Kocury (pseudo),
    w_stadku_od     DATE         DEFAULT SYSDATE,
    przydzial_myszy NUMBER(3),
    myszy_extra     NUMBER(3),
    nr_bandy        NUMBER(2)    CONSTRAINT kocury_nr_bandy_fk REFERENCES Bandy (nr_bandy)
);

CREATE TABLE Wrogowie_kocurow (
    pseudo         VARCHAR2(15) CONSTRAINT wk_pseudo_fk REFERENCES Kocury (pseudo),
    imie_wroga     VARCHAR2(15) CONSTRAINT wk_imie_wroga_fk REFERENCES Wrogowie (imie_wroga),
    data_incydentu DATE         CONSTRAINT wk_data_nn NOT NULL,
    opis_incydentu VARCHAR2(50),
    -- klucz złożony
    CONSTRAINT wk_pk PRIMARY KEY (pseudo, imie_wroga)
);

-- tutaj relacja cykliczna, więc trzeba stworzyc poniżej (dopuszczalny wyjątek)
ALTER TABLE Bandy
    ADD CONSTRAINT bandy_szef_fk FOREIGN KEY (szef_bandy) REFERENCES Kocury (pseudo);
