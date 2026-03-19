import sys, utils

characters = 0

def count(c):
    global characters
    if not c.isspace():
        characters += 1

def main():
    utils.process_text(on_char=count)
    print(characters)


if __name__ == "__main__":
    result = main()
