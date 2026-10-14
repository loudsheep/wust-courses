-- Lista 1, zadanie 2 (Postgres), wypełnianie danymi

-- Funkcje
INSERT INTO Funkcje (funkcja, min_myszy, max_myszy) VALUES ('SZEFUNIO', 90, 110);
INSERT INTO Funkcje (funkcja, min_myszy, max_myszy) VALUES ('BANDZIOR', 70, 90);
INSERT INTO Funkcje (funkcja, min_myszy, max_myszy) VALUES ('LOWCZY', 60, 70);
INSERT INTO Funkcje (funkcja, min_myszy, max_myszy) VALUES ('LAPACZ', 50, 60);
INSERT INTO Funkcje (funkcja, min_myszy, max_myszy) VALUES ('KOT', 40, 50);
INSERT INTO Funkcje (funkcja, min_myszy, max_myszy) VALUES ('MILUSIA', 20, 30);
INSERT INTO Funkcje (funkcja, min_myszy, max_myszy) VALUES ('DZIELCZY', 45, 55);
INSERT INTO Funkcje (funkcja, min_myszy, max_myszy) VALUES ('HONOROWA', 6, 25);

-- Bandy - szef jest wypełniany poniżej po uzupełnieniy tabeli Kocury
INSERT INTO Bandy (nr_bandy, nazwa, teren, szef_bandy) VALUES (1, 'SZEFOSTWO', 'CALOSC', NULL);
INSERT INTO Bandy (nr_bandy, nazwa, teren, szef_bandy) VALUES (2, 'CZARNI RYCERZE', 'POLE', NULL);
INSERT INTO Bandy (nr_bandy, nazwa, teren, szef_bandy) VALUES (3, 'BIALI LOWCY', 'SAD', NULL);
INSERT INTO Bandy (nr_bandy, nazwa, teren, szef_bandy) VALUES (4, 'LACIACI MYSLIWI', 'GORKA', NULL);
INSERT INTO Bandy (nr_bandy, nazwa, teren, szef_bandy) VALUES (5, 'ROCKERSI', 'ZAGRODA', NULL);

-- Wrogowie
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('KAZIO', 10, 'CZLOWIEK', 'FLASZKA');
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('GLUPIA ZOSKA', 1, 'CZLOWIEK', 'KORALIK');
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('SWAWOLNY DYZIO', 7, 'CZLOWIEK', 'GUMA DO ZUCIA');
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('BUREK', 4, 'PIES', 'KOSC');
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('DZIKI BILL', 10, 'PIES', NULL);
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('REKSIO', 2, 'PIES', 'KOSC');
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('BETHOVEN', 1, 'PIES', 'PEDIGRIPALL');
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('CHYTRUSEK', 5, 'LIS', 'KURCZAK');
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('SMUKLA', 1, 'SOSNA', NULL);
INSERT INTO Wrogowie (imie_wroga, stopien_wrogosci, gatunek, lapowka) VALUES ('BAZYLI', 3, 'KOGUT', 'KURA DO STADA');

-- Kocury - ustwione żeby Szef już istniał
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('MRUCZEK', 'M', 'TYGRYS', 'SZEFUNIO', NULL, DATE '2002-01-01', 103, 33, 1);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('CHYTRY', 'M', 'BOLEK', 'DZIELCZY', 'TYGRYS', DATE '2002-05-05', 50, NULL, 1);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('KOREK', 'M', 'ZOMBI', 'BANDZIOR', 'TYGRYS', DATE '2004-03-16', 75, 13, 3);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('BOLEK', 'M', 'LYSY', 'BANDZIOR', 'TYGRYS', DATE '2006-08-15', 72, 21, 2);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('RUDA', 'D', 'MALA', 'MILUSIA', 'TYGRYS', DATE '2006-09-17', 22, 42, 1);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('PUCEK', 'M', 'RAFA', 'LOWCZY', 'TYGRYS', DATE '2006-10-15', 65, NULL, 4);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('MICKA', 'D', 'LOLA', 'MILUSIA', 'TYGRYS', DATE '2009-10-14', 25, 47, 1);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('JACEK', 'M', 'PLACEK', 'LOWCZY', 'LYSY', DATE '2008-12-01', 67, NULL, 2);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('BARI', 'M', 'RURA', 'LAPACZ', 'LYSY', DATE '2009-09-01', 56, NULL, 2);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('ZUZIA', 'D', 'SZYBKA', 'LOWCZY', 'LYSY', DATE '2006-07-21', 65, NULL, 2);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('BELA', 'D', 'LASKA', 'MILUSIA', 'LYSY', DATE '2008-02-01', 24, 28, 2);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('SONIA', 'D', 'PUSZYSTA', 'MILUSIA', 'ZOMBI', DATE '2010-11-18', 20, 35, 3);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('PUNIA', 'D', 'KURKA', 'LOWCZY', 'ZOMBI', DATE '2008-01-01', 61, NULL, 3);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('LATKA', 'D', 'UCHO', 'KOT', 'RAFA', DATE '2011-01-01', 40, NULL, 4);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('DUDEK', 'M', 'MALY', 'KOT', 'RAFA', DATE '2011-05-15', 40, NULL, 4);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('KSAWERY', 'M', 'MAN', 'LAPACZ', 'RAFA', DATE '2008-07-12', 51, NULL, 4);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('MELA', 'D', 'DAMA', 'LAPACZ', 'RAFA', DATE '2008-11-01', 51, NULL, 4);
INSERT INTO Kocury (imie, plec, pseudo, funkcja, szef, w_stadku_od, przydzial_myszy, myszy_extra, nr_bandy) VALUES ('LUCEK', 'M', 'ZERO', 'KOT', 'KURKA', DATE '2010-03-01', 43, NULL, 3);

-- Bandy.szef_bandy - ustawiamy jak Kocury już istnieje
UPDATE Bandy SET szef_bandy = 'TYGRYS' WHERE nr_bandy = 1;
UPDATE Bandy SET szef_bandy = 'LYSY' WHERE nr_bandy = 2;
UPDATE Bandy SET szef_bandy = 'ZOMBI' WHERE nr_bandy = 3;
UPDATE Bandy SET szef_bandy = 'RAFA' WHERE nr_bandy = 4;

-- Wrogowie_kocurow
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('TYGRYS', 'KAZIO', DATE '2004-10-13', 'USILOWAL NABIC NA WIDLY');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('ZOMBI', 'SWAWOLNY DYZIO', DATE '2005-03-07', 'WYBIL OKO Z PROCY');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('BOLEK', 'KAZIO', DATE '2005-03-29', 'POSZCZUL BURKIEM');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('SZYBKA', 'GLUPIA ZOSKA', DATE '2006-09-12', 'UZYLA KOTA JAKO SCIERKI');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('MALA', 'CHYTRUSEK', DATE '2007-03-07', 'ZALECAL SIE');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('TYGRYS', 'DZIKI BILL', DATE '2007-06-12', 'USILOWAL POZBAWIC ZYCIA');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('BOLEK', 'DZIKI BILL', DATE '2007-11-10', 'ODGRYZL UCHO');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('LASKA', 'DZIKI BILL', DATE '2008-12-12', 'POGRYZL ZE LEDWO SIE WYLIZALA');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('LASKA', 'KAZIO', DATE '2009-01-07', 'ZLAPAL ZA OGON I ZROBIL WIATRAK');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('DAMA', 'KAZIO', DATE '2009-02-07', 'CHCIAL OBEDRZEC ZE SKORY');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('MAN', 'REKSIO', DATE '2009-04-14', 'WYJATKOWO NIEGRZECZNIE OBSZCZEKAL');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('LYSY', 'BETHOVEN', DATE '2009-05-11', 'NIE PODZIELIL SIE SWOJA KASZA');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('RURA', 'DZIKI BILL', DATE '2009-09-03', 'ODGRYZL OGON');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('PLACEK', 'BAZYLI', DATE '2010-07-12', 'DZIOBIAC UNIEMOZLIWIL PODEBRANIE KURCZAKA');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('PUSZYSTA', 'SMUKLA', DATE '2010-11-19', 'OBRZUCILA SZYSZKAMI');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('KURKA', 'BUREK', DATE '2010-12-14', 'POGONIL');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('MALY', 'CHYTRUSEK', DATE '2011-07-13', 'PODEBRAL PODEBRANE JAJKA');
INSERT INTO Wrogowie_kocurow (pseudo, imie_wroga, data_incydentu, opis_incydentu) VALUES ('UCHO', 'SWAWOLNY DYZIO', DATE '2011-07-14', 'OBRZUCIL KAMIENIAMI');
