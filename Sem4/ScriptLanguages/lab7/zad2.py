from functools import reduce
from typing import Callable, Iterable, TypeVar

T = TypeVar("T")


def forall(pred: Callable[[T], bool], iterable: Iterable[T]) -> bool:
    return all(map(pred, iterable))


def exists(pred: Callable[[T], bool], iterable: Iterable[T]) -> bool:
    return any(map(pred, iterable))


def atleast(n: int, pred: Callable[[T], bool], iterable: Iterable[T]) -> bool:
    return reduce(lambda acc, x: acc + (1 if pred(x) else 0), iterable, 0) >= n


def atmost(n: int, pred: Callable[[T], bool], iterable: Iterable[T]) -> bool:
    return reduce(lambda acc, x: acc + (1 if pred(x) else 0), iterable, 0) <= n
