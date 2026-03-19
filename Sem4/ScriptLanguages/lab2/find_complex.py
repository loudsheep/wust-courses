import sys, utils

found = False


def handle_sentence(sentence):
    global found
    if not found:
        commas = 0
        for c in sentence:
            if c == ",":
                commas += 1

        # czy jeden przecinek to już zdanie podrzene? - nie wiem bo mature cudem zdałem xd
        # jak nie to można zmienić na >= 2
        if commas >= 1:
            print(sentence)
            found = True


def main():
    utils.process_text(on_sentence=handle_sentence)


if __name__ == "__main__":
    main()
