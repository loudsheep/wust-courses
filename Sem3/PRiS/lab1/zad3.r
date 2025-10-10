a <- seq(300, 0, by = -3)
b <- c("one", "two", "three", "four", 5)
c <- c("one", "two", "three", "four", "5")
d <- rep(c(3, 1, 6), times = 4)
e <- rep(c(3, 1, 6), each = 4)
f <- c(5, 1, 4, 7)

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

print(sort(b))
print(sort(e))

print(d + f)
print(sum(d * f))
print(a[35])
print(a[67:85])
print(a[a < 100])
print(length(a[a < 100]))
