import sys, utils

longest_sentence = ""
longest_sentence_len = 0

def check_condition(sentence):
    in_word = False
    previous = ""

    for c in sentence:
        if c.isalpha():
            if not in_word:
                if previous == c.lower():
                    return False
                previous = c.lower()
                in_word = True
        else:
            in_word = False
    return True


def handle_sentence(sentence):
    if not check_condition(sentence):
        return

    global longest_sentence, longest_sentence_len
    if len(sentence) > longest_sentence_len:
        longest_sentence = sentence
        longest_sentence_len = len(sentence)

def main():
    utils.process_text(on_sentence=handle_sentence)
    print(longest_sentence)


if __name__ == "__main__":
    main()
