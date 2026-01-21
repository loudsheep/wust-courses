mieszkania <- read.csv("mieszkania.csv", sep = ";")

mieszkania$cena_m2 <- mieszkania$Cena / mieszkania$Metraz
mieszkania$droga <- mieszkania$cena_m2 > 6000

# a)
print(shapiro.test(mieszkania$cena_m2))

# b)
srodmiescie <- subset(mieszkania, Dzielnica == "Srodmiescie")
print(shapiro.test(srodmiescie$Metraz))