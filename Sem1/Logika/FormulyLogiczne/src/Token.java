import java.util.ArrayList;
import java.util.HashMap;

public class Token {
    public enum TokenType {
        AND,    // i, koniunkcja
        OR,     // lub, alternatywa
        NOT,    // negacja
        IMPL,   // implikacja
        EQU,    // rownoważność
        OPEN_PARENTHESES,   // lewy nawias
        CLOSE_PARENTHESES,  // prawy nawias
        FALSE,  // 0, fałsz
        TRUE,   // 1, prawda
        VAR,    // zmienna
    }


    public final TokenType type;
    public final char character;
    public final boolean value;

    public Token(TokenType type, char character, boolean value) {
        this.type = type;
        this.character = character;
        this.value = value;
    }

    public Token(TokenType type, char character) {
        this(type, character, false);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", character='" + character +
                "', value=" + value +
                '}';
    }

    public static ArrayList<Token> tokenize(String input, HashMap<Character, Boolean> vars) {
        ArrayList<Token> tokens = new ArrayList<>();

        int i = 0;
        while (i < input.length()) {
            char currentChar = input.charAt(i);
            switch (currentChar) {
                case '&':
                    tokens.add(new Token(Token.TokenType.AND, currentChar));
                    break;
                case '|':
                    tokens.add(new Token(Token.TokenType.OR, currentChar));
                    break;
                case '!':
                    tokens.add(new Token(Token.TokenType.NOT, currentChar));
                    break;
                case '>':
                    tokens.add(new Token(Token.TokenType.IMPL, currentChar));
                    break;
                case '=':
                    tokens.add(new Token(Token.TokenType.EQU, currentChar));
                    break;
                case '(':
                    tokens.add(new Token(Token.TokenType.OPEN_PARENTHESES, currentChar));
                    break;
                case ')':
                    tokens.add(new Token(Token.TokenType.CLOSE_PARENTHESES, currentChar));
                    break;
                case '0':
                    tokens.add(new Token(Token.TokenType.FALSE, currentChar, false));
                    break;
                case '1':
                    tokens.add(new Token(Token.TokenType.TRUE, currentChar, true));
                    break;
                case ' ':
                    break;
                default:
                    if (Character.isAlphabetic(currentChar)) {
                        if (vars.containsKey(currentChar)) {
                            tokens.add(new Token(Token.TokenType.VAR, currentChar, vars.get(currentChar)));
                        } else {
                            throw new IllegalArgumentException("Undefined variable '" + currentChar + "' at position " + i);
                        }
                    } else {
                        throw new IllegalArgumentException("Illegal character '" + currentChar + "' at position " + i);
                    }
                    break;
            }
            i++;
        }

        return tokens;
    }
}