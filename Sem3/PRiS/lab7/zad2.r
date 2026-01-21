# Wykształcenie Wyższe Wykształcenie średnie
# Kobiety 200 300
# Mężczyźni 150 350
dane_wyksztalcenie <- matrix(c(200, 150, 300, 350), nrow = 2)
dimnames(dane_wyksztalcenie) <- list(
  Plec = c("Kobiety", "Mezczyzni"),
  Wyksztalcenie = c("Wyzsze", "Srednie")
)

# a)
test_niezaleznosci <- chisq.test(dane_wyksztalcenie)
print(test_niezaleznosci$expected)

# b) c) d) e)
print(test_niezaleznosci)

# f)
fisher.test(dane_wyksztalcenie)

# g)
prop.test(dane_wyksztalcenie)