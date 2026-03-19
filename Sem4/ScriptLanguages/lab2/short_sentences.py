import sys, utils


def handle_sentence(sentence):
    words = 0
    in_word = False

    for c in sentence:
        if c.isalpha():
            if not in_word:
                words += 1
                in_word = True
        else:
            in_word = False

    if words < 5:
        print(sentence)


def main():
    utils.process_text(on_sentence=handle_sentence)


if __name__ == "__main__":
    main()
