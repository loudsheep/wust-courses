dane <- read.csv("waga1.csv", sep=";")

# Zad 2
t.test(dane$wzrost, mu=170)

# Zad 3
t.test(dane$wzrost, conf.level = 0.90)$conf.int

# Zad 4
kobiety <- subset(dane, plec == 1)
t.test(kobiety$wzrost, mu = 160)

# Zad 5
t.test(kobiety$wzrost, conf.level = 0.98)$conf.int

# Zad 6
mezczyzni <- subset(dane, plec == 0)
n_mez <- nrow(mezczyzni)
k_wysocy <- sum(mezczyzni$wzrost > 180)
prop.test(k_wysocy, n_mez, p = 0.25)

# Zad 7
prop.test(k_wysocy, n_mez, conf.level = 0.96)$conf.int