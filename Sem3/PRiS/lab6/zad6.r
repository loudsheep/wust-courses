dane <- read.csv("waga1.csv")
dane$roznica_wagi <- dane$waga_po - dane$waga_przed

roznica_m <- dane$roznica_wagi[dane$plec == 0]
t.test(roznica_m, mu = 4)