import sys, utils


# find sentences that are at least 6 words long, and no two adjacent words start with the same letter (case-insensitive)
def handle_sentence(sentence):
    words = 0
    in_word = False
    previous = ""

    for c in sentence:
        if c.isalpha():
            if not in_word:
                if previous == c.lower():
                    return
                previous = c.lower()
                in_word = True
                words += 1
        else:
            in_word = False
            previous = ""
    if words > 6:
        print(sentence, "\n\n")


def main():
    utils.process_text(on_sentence=handle_sentence)
    # print(f"Total sentences processed: {count}")


if __name__ == "__main__":
    main()
