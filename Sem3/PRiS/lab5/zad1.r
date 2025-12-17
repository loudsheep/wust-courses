n <- 100
avg <- 109
var <- 225
sd <- sqrt(var)

k_success <- 30 # liczba studentów z IQ > 115

# a)
# i)
p0 <- 0.35
p_hat <- k_success / n
z_stat <- (p_hat - p0) / sqrt(p0 * (1 - p0) / n)
p_val_z <- 2 * (1 - pnorm(abs(z_stat)))
cat("a) Statystyka z:", z_stat, "Wartość p:", p_val_z, "\n")

# ii)
prop.test(k_success, n, p = p0)

# b)
# i)
z_kryt <- qnorm(1 - 0.01 / 2) # dla 99%
blad <- z_kryt * sqrt(p_hat * (1 - p_hat) / n)
ci_norm <- c(p_hat - blad, p_hat + blad)
print(ci_norm)

# ii)
prop.test(k_success, n, conf.level = 0.99)$conf.int

# c)
z_kryt_90 <- qnorm(1 - 0.10 / 2)
blad_sr <- z_kryt_90 * (sd / sqrt(n))
ci_mean <- c(avg - blad_sr, avg + blad_sr)
print(ci_mean)

# d)
t_kryt_90 <- qt(1 - 0.10 / 2, df = n - 1)
blad_sr_t <- t_kryt_90 * (sd / sqrt(n))
ci_mean_t <- c(avg - blad_sr_t, avg + blad_sr_t)
print(ci_mean_t)

# e)
# i)
mu0 <- 115
z_stat_mean <- (avg - mu0) / (sd / sqrt(n))
p_val_mean <- 2 * (1 - pnorm(abs(z_stat_mean)))
cat("e) i) Statystyka z dla średniej:", z_stat_mean, "Wartość p:", p_val_mean, "\n")

# ii)
t_stat_mean <- (avg - mu0) / (sd / sqrt(n))
p_val_t <- 2 * (1 - pt(abs(t_stat_mean), df = n - 1))
cat("e) ii) Statystyka t dla średniej:", t_stat_mean, "Wartość p:", p_val_t, "\n")
