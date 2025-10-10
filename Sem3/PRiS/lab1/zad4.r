a <- matrix(c(-3, 4, 1, -5, -2, 3), nrow = 2)
b <- cbind(c(1, 3, 5), c(2, 4, 6))
c <- rbind(c(7, -3), c(-2, 1))
d <- rbind(c(1, 2, 4), c(3, 5, 7), c(2, 3, 2))


print(a + b)
print(t(a) + b)
print(b %*% a)
print(b * b)
print(solve(c))
print(c %*% solve(c))
print(solve(c, b))
print(solve(d, b))