mu <- 170
sigma <- sqrt(12)

# i) P(X > 180)
prob_a <- 1 - pnorm(180, mean = mu, sd = sigma)
cat("P(X > 180) =", prob_a, "\n")

# ii) P(X < 165)
prob_b <- pnorm(165, mean = mu, sd = sigma)
cat("P(X < 165) =", prob_b, "\n")

# iii) P(155 < X < 190) = P(X < 190) - P(X <= 155)
prob_c <- pnorm(190, mean = mu, sd = sigma) - pnorm(155, mean = mu, sd = sigma)
cat("P(155 < X < 190) =", prob_c, "\n")

# iv) Znaleźć k, taki że P(X > k) = 0.1
# P(X > k) = 1 - P(X <= k) = 0.1 => P(X <= k) = 0.9
k_value <- qnorm(0.9, mean = mu, sd = sigma)
cat("k takie że P(X > k) = 0.1 to k =", k_value, "\n")
