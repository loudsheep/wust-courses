# a)
df <- read.csv("waga1.csv", sep = ";")

# b)
print(head(df, 5))

# c)
str(df)

# d)
print(mean(df$Wzrost))
print(mean(df$Waga_przed))

# e)
df$bmi <- df$Waga_przed / ((df$Wzrost / 100) ^ 2)

# f)
women_high_bmi <- subset(df, plec == 1 & bmi > 25)
cat("Liczba kobiet z bmi>25:", nrow(women_high_bmi), "\n")

# g)
men_df <- subset(df, plec == 0)
cat("Liczba mężczyzn:", nrow(men_df), "\n")

# h)
n_above_175 <- sum(df$Wzrost > 175)
cat("Liczba osób wyższych niż 175 cm:", n_above_175, "\n")
