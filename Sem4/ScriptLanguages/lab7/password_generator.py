import random
import string
from typing import Optional, Iterable


class PasswordGenerator:
    def __init__(
        self, length: int, charset: Optional[Iterable[str]] = None, count: int = 1
    ):
        self.length = length
        self.charset = (
            list(charset)
            if charset is not None
            else list(string.ascii_letters + string.digits)
        )
        self.count = count
        self.generated = 0

    def __iter__(self):
        return self

    def __next__(self) -> str:
        if self.generated >= self.count:
            raise StopIteration

        password = "".join(random.choice(self.charset) for _ in range(self.length))

        self.generated += 1
        return password


def tests() -> None:
    gen = PasswordGenerator(length=8, count=3)

    print(next(gen))
    print(next(gen))
    print(next(gen))
    try:
        print(next(gen))
    except StopIteration:
        print("StopIteration exception occured")

    gen = PasswordGenerator(length=10, count=5)

    for password in gen:
        print(password)


if __name__ == "__main__":
    tests()
