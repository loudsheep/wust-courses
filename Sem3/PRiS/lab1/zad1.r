a <- c(1, 4, 6, 13, -10, 8)
b <- seq(1, 101, 2)
c <- rep(c(4, 7, 9), each = 3)
d <- c("czy", "to", "jest", "wektor", NA)
e <- c("czy", "to", "jest", "wektor", "NA")
f <- rep(c(4, 7, 9), times = 6)

vectors <- list(a, b, c, d, e, f)

for (i in seq_along(vectors)) {
  vec <- vectors[[i]]
  cat("Wektor", i, ":\n")
  cat("Długość:", length(vec), "\n")
  cat("Typ danych:", class(vec), "\n")
  cat("Element najmniejszy:", min(vec, na.rm = TRUE), "\n")
  cat("Element największy:", max(vec, na.rm = TRUE), "\n")

  if (is.numeric(vec)) {
    cat("Suma elementów:", sum(vec, na.rm = TRUE), "\n")
  }
  cat("\n")
}

print(sort(d))
print(sort(e))

print(a + f)
print(a * f)
print(a + c)
print(a + 10)
print(a * 15)
print(b[26])
print(f[6:10])
print(length(b[b > 50]))