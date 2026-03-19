import sys


def clean_line(line):
    cleaned = ""
    in_space = False

    for c in line:
        if c.isspace():
            if not in_space:
                cleaned += " "
                in_space = True
        else:
            cleaned += c
            in_space = False

    return cleaned.strip()


def main():
    header_buffer = ""
    line_count = 0
    empty_lines = 0
    has_preable = False

    current_line = ""
    c = sys.stdin.read(1)

    # check na preambule
    while c and line_count < 10:
        if c == "\n":
            line_count += 1

            if current_line.strip() == "":
                empty_lines += 1
            else:
                empty_lines = 0
            
            header_buffer += current_line + "\n"
            current_line = ""

            if empty_lines == 2:
                has_preable = True
                break
        else:
            current_line += c

        c = sys.stdin.read(1)

    if not has_preable:
        temp_line = ""
        for char in header_buffer:
            if char == '\n':
                if temp_line.strip() == "-----":
                    return
                print(clean_line(temp_line))
                temp_line = ""
            else:
                temp_line += char

    # content
    line = current_line
    while c:
        c = sys.stdin.read(1)
        if c == "\n" or not c:
            if line.strip() == "-----":
                return

            print(clean_line(line))
            line = ""
        else:
            line += c
    
    if line:
        if line.strip() == "-----":
            return
        print(clean_line(line))



if __name__ == "__main__":
    main()
