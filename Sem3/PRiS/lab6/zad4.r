dane <- read.csv("waga1.csv")
dane$roznica_wagi <- dane$waga_po - dane$waga_przed

wzrost_k <- dane$wzrost[dane$plec == 1]
wzrost_m <- dane$wzrost[dane$plec == 0]
t.test(wzrost_m, wzrost_k, mu = 5)