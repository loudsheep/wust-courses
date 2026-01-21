bakterie <- read.csv("bakteria.csv", sep = ";", dec = ",")

# a)
plot(bakterie$czas, bakterie$masa)

# b)
model_bak <- lm(log(masa) ~ czas, data = bakterie)
print(summary(model_bak))

# c)
log_masa_pred <- predict(model_bak, data.frame(czas = 10))
masa_pred <- exp(log_masa_pred)
print(masa_pred)