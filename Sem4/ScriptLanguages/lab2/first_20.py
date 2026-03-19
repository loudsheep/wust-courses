import sys, utils

count = 0

def handle_sentence(sentence):
    global count
    if count < 20:
        print(sentence)
        count += 1

def main():
    utils.process_text(on_sentence=handle_sentence)

if __name__ == '__main__':
    main()
