import sys


def process_text(on_sentence=None, on_paragraph=None, on_char=None):
    sentence = ""
    in_empty_space = False

    try:
        # Przetwarzanie linia po linii
        for line in sys.stdin:
            if on_char:
                for c in line:
                    on_char(c)

            if line.strip() == "":
                # Pusty wiersz -> koniec akapitu i ew wymuszenie końca zdania
                if not in_empty_space:
                    if on_paragraph:
                        on_paragraph()
                    if sentence.strip() and on_sentence:
                        on_sentence(sentence.strip())
                    sentence = ""
                in_empty_space = True
            else:
                in_empty_space = False
                for c in line:
                    sentence += c
                    # .!? -> Koniec zdania
                    if c in ".!?":
                        if on_sentence:
                            on_sentence(sentence.strip())
                        sentence = ""

        if sentence.strip() and on_sentence:
            on_sentence(sentence.strip())

    except Exception as e:
        sys.stderr.write("Blad przetwarzania: " + str(e) + "\n")
