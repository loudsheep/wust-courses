a <- c(1, 4, 6, 13, -10, 8)
b <- seq(1, 101, 2)
c <- rep(c(4, 7, 9), each=3)
d <- c("czy", "to", "jest", "wektor", NA)
e <- c("czy", "to", "jest", "wektor", "NA")
f <- rep(c(4, 7, 9), times=5)


# Dla każdego z powyższych wektorów 
# ii) Dla każdego wektora korzystając z odpowiednich funkcji podać długość, typ danych, element “najmniejszy” oraz “największy”, sumę elementów.
# iii) Posortować wektory d) oraz e)
# iv) Wyznaczyć a) a+f, b) a*f, c) a+c, d) a+10, e) 15a f) 26-ty element wektora b, g) 6-ty do 10-tego elementu (włącznie) wektora f.
# v) Które elementy w wektorze b, oraz ile, jest większe niż 50?

wektory <- list(a=a, b=b, c=c, d=d, e=e, f=f)

sapply(wektory, function(x) {
  print(paste("Wektor:", deparse(substitute(x))))
  print(paste("Długość:", length(x)))
  print(paste("Typ danych:", class(x)))
  print(paste("Najmniejszy element:", min(x, na.rm = TRUE)))
  print(paste("Największy element:", max(x, na.rm = TRUE)))
  # print(paste("Suma elementów:", sum(x, na.rm = TRUE)))

  print("-----")
})