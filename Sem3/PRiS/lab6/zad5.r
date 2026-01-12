dane <- read.csv("waga1.csv")
dane$roznica_wagi <- dane$waga_po - dane$waga_przed

przytyli <- sum(dane$roznica_wagi > 0)
n_total <- nrow(dane)
binom.test(przytyli, n_total, p = 0.8)