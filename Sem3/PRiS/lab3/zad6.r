n <- 180
p <- 1 / 6

# i) P(X = 27)
prob_a <- dbinom(27, size = n, prob = p)
cat("P(X = 27) =", prob_a, "\n")

# ii) P(X >= 32) = 1 - P(X <= 31)
prob_b <- 1 - pbinom(31, size = n, prob = p)
cat("P(X >= 32) =", prob_b, "\n")

# iii) P(X < 29) = P(X <= 28)
prob_c <- pbinom(28, size = n, prob = p)
cat("P(X < 29) =", prob_c, "\n")

# iv) P(25 <= X <= 33) = P(X <= 33) - P(X <= 24)
prob_d <- pbinom(33, size = n, prob = p) - pbinom(24, size = n, prob = p)
cat("P(25 <= X <= 33) =", prob_d, "\n")