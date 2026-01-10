# a)

# i)
k <- c(220, 165)
n <- c(520, 480)
p_hat <- sum(k)/sum(n)
z_stat <- (k[1]/n[1] - k[2]/n[2]) / sqrt(p_hat * (1 - p_hat) * (1/n[1] + 1/n[2]))
p_val <- 2 * (1 - pnorm(abs(z_stat)))
z_stat; p_val

# ii)
prop.test(x = c(220, 165), n = c(520, 480))


# b)
tablica <- matrix(c(520-220, 220, 480-165, 165), nrow = 2, byrow = TRUE)
rownames(tablica) <- c("Kobieta", "Mezczyzna")
colnames(tablica) <- c("Brak wyzszego", "Wyzsze")
tablica

# c)

chisq.test(tablica)
fisher.test(tablica)

# d)
z_stat_d <- (166 - 174) / sqrt(100/520 + 121/480)
p_val_d <- 2 * pnorm(z_stat_d)
z_stat_d; p_val_d