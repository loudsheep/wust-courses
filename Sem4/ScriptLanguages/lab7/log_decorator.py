from __future__ import annotations

import inspect
import logging
import time
from datetime import datetime
from functools import  wraps
from typing import Any, Callable

def log(level: int = logging.INFO) -> Callable[[Any], Any]:
    def decorator(obj: Any) -> Any:
        if inspect.isclass(obj):
            original_init = obj.__init__

            @wraps(original_init)
            def wrapped_init(self: Any, *args: Any, **kwargs: Any) -> None:
                logger = logging.getLogger(obj.__module__)
                logger.log(
                    level,
                    "instantiate class=%s at=%s args=%r kwargs=%r",
                    obj.__name__,
                    datetime.now().isoformat(timespec="seconds"),
                    args,
                    kwargs,
                )
                original_init(self, *args, **kwargs)

            obj.__init__ = wrapped_init
            return obj

        if callable(obj):

            @wraps(obj)
            def wrapper(*args: Any, **kwargs: Any) -> Any:
                logger = logging.getLogger(obj.__module__)
                started_at = datetime.now().isoformat(timespec="seconds")
                started_perf = time.perf_counter()

                logger.log(
                    level,
                    "call fn=%s at=%s args=%r kwargs=%r",
                    obj.__name__,
                    started_at,
                    args,
                    kwargs,
                )

                try:
                    result = obj(*args, **kwargs)
                except Exception:
                    duration_ms = (time.perf_counter() - started_perf) * 1000
                    logger.exception(
                        "error fn=%s duration_ms=%.3f",
                        obj.__name__,
                        duration_ms,
                    )
                    raise

                duration_ms = (time.perf_counter() - started_perf) * 1000
                logger.log(
                    level,
                    "return fn=%s duration_ms=%.3f value=%r",
                    obj.__name__,
                    duration_ms,
                    result,
                )
                return result

            return wrapper

        raise TypeError("@log can decorate only functions or classes")

    return decorator



@log(logging.INFO)
def add(a: int, b: int) -> int:
    return a + b


@log(logging.DEBUG)
class Point:
    def __init__(self, x: int, y: int):
        self.x = x
        self.y = y


if __name__ == "__main__":
    logging.basicConfig(
        level=logging.DEBUG,
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    )

    log(logging.ERROR)(add)(5, 10)

    _ = add(7, 11)
    _ = Point(2, 5)
    _ = Point(3, 4)
