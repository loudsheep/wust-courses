mieszkania <- read.csv("mieszkania.csv", sep = ";")

# a)
mieszkania$cena_m2 <- mieszkania$Cena / mieszkania$Metraz
mieszkania$droga <- mieszkania$cena_m2 > 6000

# b)
tabela_mieszkania <- table(mieszkania$Dzielnica, mieszkania$droga)
print(chisq.test(tabela_mieszkania))
