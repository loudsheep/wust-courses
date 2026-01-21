mieszkania <- read.csv("mieszkania.csv", sep = ";")

mieszkania$cena_m2 <- mieszkania$Cena / mieszkania$Metraz
mieszkania$droga <- mieszkania$cena_m2 > 6000

# a)
model <- lm(Cena ~ Metraz, data = mieszkania)
print(summary(model))

# b)
plot(mieszkania$Metraz, mieszkania$Cena)
print(abline(model, col = "red"))

# c)
print(shapiro.test(model$residuals))

# d)
print(predict(model, data.frame(Metraz = 80)))
