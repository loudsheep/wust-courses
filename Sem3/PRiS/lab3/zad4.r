lambda <- 4

# i) P(Czas > 30s) = P(T > 0.5 min)
prob_a <- 1 - pexp(0.5, rate = lambda)
cat("P(Czas > 30s) =", prob_a, "\n")

# ii) P(Czas < 20s) = P(T < 1/3 min)
prob_b <- pexp(1 / 3, rate = lambda)
cat("P(Czas < 20s) =", prob_b, "\n")

# iii) P(40s < Czas < 80s) = P(2/3 min < T < 4/3 min) = P(T < 4/3 min) - P(T <= 2/3 min)
prob_c <- pexp(4 / 3, rate = lambda) - pexp(2 / 3, rate = lambda)
cat("P(40s < Czas < 80s) =", prob_c, "\n")

# iv) Znaleźć t, taki że P(T > t) = 0.2
# P(T > t) = 1 - P(T <= t) = 0.2 => P(T <= t) = 0.8
t_value <- qexp(0.8, rate = lambda) * 60
cat("t takie że P(T > t) = 0.2 to t =", t_value, "sekund\n")

# v) Wykres rozkładu
t_seq <- seq(0, 3, by = 0.01)
f_t <- dexp(t_seq, rate = lambda)
plot(t_seq, f_t, type = "l")
