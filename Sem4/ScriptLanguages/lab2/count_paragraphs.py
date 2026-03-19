import sys, utils

paragraphs = 0

def count():
    global paragraphs
    paragraphs += 1

def main():
    utils.process_text(on_paragraph=count)
    print(paragraphs)


if __name__ == "__main__":
    main()
