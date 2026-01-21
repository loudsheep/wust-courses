# Frekwencja 171 200 168 213 226 222
obserwowane <- c(171, 200, 168, 213, 226, 222)

# a)
oczekiwane <- rep(sum(obserwowane) / length(obserwowane), length(obserwowane))
print(oczekiwane)

# b) c) d) e)
test_kostki <- chisq.test(obserwowane)
print(test_kostki)