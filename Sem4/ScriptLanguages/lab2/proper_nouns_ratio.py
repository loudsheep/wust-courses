import sys, utils

total_sentences = 0
sentences_with_pn = 0

def check_condition(sentence):
    in_word = False
    is_first_word = True

    for c in sentence:
        if c.isalpha():
            in_word = True

            if not is_first_word and c.isupper():
                return True

            is_first_word = False
        else:
            in_word = False
    return False

def handle_sentence(sentence):
    global total_sentences, sentences_with_pn
    total_sentences += 1

    if check_condition(sentence):
        sentences_with_pn += 1

def main():
    utils.process_text(on_sentence=handle_sentence)
    result = sentences_with_pn / total_sentences
    sys.stdout.write(str(result) + "\n")

if __name__ == '__main__':
    main()
