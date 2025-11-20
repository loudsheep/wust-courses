lambda <- 3 * 2 # 6 samochodów na 2 tygodnie

# i) P(X = 5)
prob_a <- dpois(5, lambda = lambda)
cat("P(X = 5) =", prob_a, "\n")

# ii) P(X >= 4) = 1 - P(X <= 3)
prob_b <- 1 - ppois(3, lambda = lambda)
cat("P(X >= 4) =", prob_b, "\n")

# iii) P(3 <= X <= 5) = P(X <= 5) - P(X <= 2)
prob_c <- ppois(5, lambda = lambda) - ppois(2, lambda = lambda)
cat("P(3 <= X <= 5) =", prob_c, "\n")

# iv) Wykres rozkładu
x <- 0:30
y <- dpois(x, lambda = lambda)
plot(x, y, type = "h")
