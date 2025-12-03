wykl <- rexp(3000, rate = 1 / 100)
hist(wykl, main = "Histogram - Rozkład Wykładniczy")
d_wykl <- density(wykl)