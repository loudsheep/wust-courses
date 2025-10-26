# a)
df <- read.csv("mieszkania.csv", sep = ";")

# b)
print(head(df, 6))

# c)
str(df)

# d)
avg_price <- mean(df$Cena)
cat("Średnia cena mieszkań:", avg_price, "\n")
avg_area <- mean(df$Metraz)
cat("Średnia powierzchnia mieszkań:", avg_area, "\n")

# e)
df$Cena_za_m2 <- df$Cena / df$Metraz
print(head(df, 6))

# f)
subset(df, Dzielnica == "Psie Pole" & Cena < 40000)

# g)
subset(df, Dzielnica == "Srodmiescie" & Metraz > 60)

# h)
sum(df$Metraz > 60 & df$Cena < 350000)