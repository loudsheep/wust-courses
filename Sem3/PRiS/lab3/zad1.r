n <- 6
p <- 0.5


# i) P(X = 5)
prob_a <- dbinom(5, size = n, prob = p)
cat("P(X = 5) =", prob_a, "\n")

# ii) P(X >= 3) = 1 - P(X <= 2)
prob_b <- 1 - pbinom(2, size = n, prob = p)
cat("P(X >= 3) =", prob_b, "\n")

# iii) P(2 <= X <= 4) = P(X <= 4) - P(X <= 2 - 1)
prob_c <- pbinom(4, size = n, prob = p) - pbinom(1, size = n, prob = p)
cat("P(2 <= X <= 4) =", prob_c, "\n")


# iv) Wykres rozkładu
x <- 0:6
y <- dbinom(x, size = n, prob = p)
plot(x, y, type = "h")
