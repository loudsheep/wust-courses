min <- 4
max <- 12

# i) P(X = 7)
prob_a <- dunif(7, min = min, max = max)
cat("P(X = 7) =", prob_a, "\n")

# ii) P(5 < X < 11) = P(X < 11) - P(X <= 5)
prob_b <- punif(11, min = min, max = max) - punif(5, min = min, max = max)
cat("P(5 < X < 11) =", prob_b, "\n")

# iii) P(X > 10)
prob_c <- 1 - punif(10, min = min, max = max)
cat("P(X > 10) =", prob_c, "\n")

# iv) Znaleźć x, taki że P(X > x) = 0.6
x_value <- qunif(0.4, min = min, max = max)
cat("x takie że P(X > x) = 0.6 to x =", x_value, "\n")
