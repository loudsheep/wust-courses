dane <- read.csv("waga1.csv")
dane$roznica_wagi <- dane$waga_po - dane$waga_przed

dane$ponad70 <- as.numeric(dane$waga_po > 70)
tab_waga <- table(dane$plec, dane$ponad70)
prop.test(tab_waga)