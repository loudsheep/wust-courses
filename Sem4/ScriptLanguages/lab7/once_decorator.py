def once():
    def decorator(func):
        if not callable(func):
            raise TypeError(
                "The @once decorator can only be applied to callable objects"
            )

        called = False

        def wrapper(*args, **kwargs):
            nonlocal called
            if not called:
                called = True
                return func(*args, **kwargs)

        return wrapper

    return decorator


@once()
def greet(name):
    print(f"Hello, {name}!")


@once()
def greet_again(name):
    print(f"Hello again, {name}!")


if __name__ == "__main__":

    greet("Alice")
    greet("Bob")
    greet_again("Charlie")
    greet_again("David")
    greet_again("David")
    greet_again("David")
    greet_again("David")
