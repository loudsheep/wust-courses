# a)
oceny <- data.frame(
    Imie   = c("Krzysztof", "Maria", "Henryk", "Anna"),
    Plec   = c("m", "k", "m", "k"),
    Analiza = c(3.5, 4.5, 5.0, 4.5),
    Algebra = c(4.0, 5.0, 4.0, 3.5)
)

# b)
print(oceny[1:2, ], row.names = FALSE)

# c)
str(oceny)

# d)
srednia_analiza <- mean(oceny$Analiza)
print(srednia_analiza)

# e)
oceny$Srednia <- rowMeans(oceny[, c("Analiza", "Algebra")])
print(oceny)

# f)
kobiety <- subset(oceny, Plec == "k")
print(kobiety)

# g)
min_45 <- subset(oceny, Analiza >= 4.5 | Algebra >= 4.5)
print(min_45)

# h)
ile_analiza_45 <- sum(oceny$Analiza >= 4.5)
print(ile_analiza_45)