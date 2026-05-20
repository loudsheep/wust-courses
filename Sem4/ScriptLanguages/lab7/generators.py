from __future__ import annotations

from functools import lru_cache
from itertools import islice
from typing import Callable, Generator, TypeVar

T = TypeVar("T")


def make_generator(f: Callable[[int], T]) -> Generator[T, None, None]:
    def _generator() -> Generator[T, None, None]:
        n = 1
        while True:
            yield f(n)
            n += 1

    return _generator()


def make_generator_mem(f: Callable[[int], T]) -> Generator[T, None, None]:
    cached_f = lru_cache(maxsize=None)(f)
    return make_generator(cached_f)


def fibonacci(n: int) -> int:
    if n <= 0:
        raise ValueError("n must be positive")
    if n <= 2:
        return 1

    a, b = 1, 1
    for _ in range(3, n + 1):
        a, b = b, a + b
    return b


@lru_cache(maxsize=None)
def fibonacci_recursive(n: int) -> int:
    if n <= 0:
        raise ValueError("n must be positive")
    if n <= 2:
        return 1
    return fibonacci_recursive(n - 1) + fibonacci_recursive(n - 2)


# customowa funkcja rzeby wziac tylko n elems z generatora
def take(generator: Generator[T, None, None], count: int) -> list[T]:
    return list(islice(generator, count))


def tests() -> None:
    fib_gen = make_generator(fibonacci)
    print("4a fibonacci:", take(fib_gen, 10))

    arithmetic_gen = make_generator(lambda n: 3 + (n - 1) * 4)
    geometric_gen = make_generator(lambda n: 2 * (3 ** (n - 1)))
    pow_gen = make_generator(lambda n: n**3)

    print("4b arithmetic:", take(arithmetic_gen, 10))
    print("4b geometric:", take(geometric_gen, 10))
    print("4b pow:", take(pow_gen, 10))

    fib_rec_gen = make_generator_mem(fibonacci_recursive)
    print("Memoized recursive fibonacci:", take(fib_rec_gen, 12))


if __name__ == "__main__":
    tests()
