dane <- read.csv("waga1.csv")
dane$roznica_wagi <- dane$waga_po - dane$waga_przed

t.test(dane$roznica_wagi, mu = 2)