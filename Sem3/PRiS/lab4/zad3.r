values <- c(0, 1, 2, 3)
prob <- c(0.15, 0.25, 0.5, 0.1)

rzuty_c <- sample(values, size = 600, replace = TRUE, prob = prob)

print(mean(rzuty_c))
print(var(rzuty_c))