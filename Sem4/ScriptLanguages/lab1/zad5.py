import pyfiglet

# pyfiglet.print_figlet("Języki skryptowe")

text = input("Podaj tekst do wygenerowania: ")
font = input("Podaj czcionkę (np. slant, standard, big): ")

try:
    ascii_art = pyfiglet.figlet_format(text, font=font)
    print(ascii_art)
except pyfiglet.FontNotFound:
    print(f"Czcionka '{font}' nie została znaleziona. Użyj jednej z dostępnych czcionek.")
