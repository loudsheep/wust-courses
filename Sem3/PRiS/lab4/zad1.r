jedn <- runif(5000, min = 0, max = 1)
hist(jedn, main = "Histogram - Rozkład Jednostajny")
d_jedn <- density(jedn)


wykl <- rexp(5000, rate = 1)
hist(wykl, main = "Histogram - Rozkład Wykładniczy")
d_wykl <- density(wykl)