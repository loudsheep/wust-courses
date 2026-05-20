from functools import reduce
from typing import Any


def acronym(words: list[str]) -> str:
    return "".join(map(lambda w: w[0], words))


def median(numbers: list[float]) -> float:
    s = sorted(numbers)
    n = len(s)
    mid = n // 2

    return s[mid] if n % 2 == 1 else (s[mid - 1] + s[mid]) / 2


def pierwiastek(x: float, epsilon: float = 0.1) -> float:
    def newton(y: float) -> float:
        return y if abs(y * y - x) < epsilon else newton((y + x / y) / 2)

    return newton(x if x > 1 else 1)


def make_alpha_dict(text: str) -> dict[str, list[str]]:
    words = text.split()

    return {
        ch: list(filter(lambda w: ch in w, words))
        for ch in sorted(set(filter(str.isalpha, text)))
    }


def flatten(seq: list[Any] | tuple[Any, ...]) -> list[Any]:
    return reduce(
        lambda acc, el: acc + (flatten(el) if isinstance(el, (list, tuple)) else [el]),
        seq,
        [],
    )


def group_anagrams(words: list[str]) -> dict[str, list[str]]:
    keys = list(map(lambda w: "".join(sorted(w)), words))

    return {
        key: list(filter(lambda w: "".join(sorted(w)) == key, words))
        for key in dict.fromkeys(keys)
    }
