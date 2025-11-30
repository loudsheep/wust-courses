rzuty_a <- ceiling(runif(600) * 6)

print(mean(rzuty_a))
print(var(rzuty_a))

tabela <- table(rzuty_a)
print(tabela)

df <- as.data.frame(tabela)
print(df)
print(var(tabela))

rzuty_b <- sample(1:6, size = 600, replace = TRUE)
