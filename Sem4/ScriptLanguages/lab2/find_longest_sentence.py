import sys, utils

longest_sentence = ""
longest_sentence_len = 0

def handle_sentence(sentence):
    global longest_sentence, longest_sentence_len
    if len(sentence) > longest_sentence_len:
        longest_sentence = sentence
        longest_sentence_len = len(sentence)

def main():
    utils.process_text(on_sentence=handle_sentence)
    print(longest_sentence)


if __name__ == "__main__":
    main()
