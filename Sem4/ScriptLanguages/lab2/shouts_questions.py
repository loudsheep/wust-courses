import sys, utils

def handle_sentence(sentence):
    clean_sentence = sentence.strip()
    if clean_sentence == "":
        return
    
    last_char = clean_sentence[-1]
    if last_char in "!?":
        print(sentence)


def main():
    utils.process_text(on_sentence=handle_sentence)

if __name__ == "__main__":
    main()
