# a)
prob_exp <- rexp(1000, rate = 1)

# b)

# i)
print(ks.test(prob_exp, "pnorm", mean = 1, sd = 1))
# ii)
print(ks.test(prob_exp, "pexp", rate = 1))

# c)
prob_gamma <- rgamma(1000, shape = 100, scale = 1)

# d)

# i)
print(ks.test(prob_gamma, "pnorm", mean = 100, sd = 10))
# ii)
print(ks.test(prob_gamma, "pgamma", shape = 100, scale = 1))
