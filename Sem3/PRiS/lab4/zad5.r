# g(x) = 0.5x dla x z [0,2], 0 w p.p.
# zatem F(x) = 0 dla x<0, F(x) = 0.25x^2 dla x z [0,2], F(x) = 1 dla x>2
# zatem F^{-1}(u) = 2sqrt(u) dla u z [0,1]
n <- 200
u <- runif(n)
x_inv <- 2 * sqrt(u)

hist(x_inv, main = "Metoda odwracania")


results <- numeric(0)
while (length(results) < n) {
  x <- runif(1, min = 0, max = 2)
  y <- runif(1, min = 0, max = 1)

  if (y <= 0.5 * x) {
    results <- c(results, x)
  }
}

hist(results, main = "Metoda akceptacji-odrzucenia")
