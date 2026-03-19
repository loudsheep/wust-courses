import sys, utils


def handle_sentence(sentence):
    target_words = " i oraz ale że lub "
    found_count = 0
    current_word = ""

    for c in sentence:
        if c.isalpha():
            current_word += c.lower()
        else:
            if current_word != "":
                if f" {current_word} " in target_words:
                    found_count += 1
                current_word = ""
    
    if current_word != "":
        if f" {current_word} " in target_words:
            found_count += 1

    if found_count >= 2:
        print(sentence)

def main():
    utils.process_text(on_sentence=handle_sentence)


if __name__ == "__main__":
    main()
