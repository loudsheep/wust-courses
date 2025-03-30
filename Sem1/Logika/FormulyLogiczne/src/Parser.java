import java.util.ArrayList;
import java.util.ListIterator;

public class Parser {
    private static ListIterator<Token> tokenIter;

    public static Node parseTokens(ArrayList<Token> tokenList) {
        tokenIter = tokenList.listIterator();
        Node result = expression(tokenIter.next());

        if (tokenIter.hasNext()) // unreachable / unexpected tokens found
            throw new IllegalArgumentException("Syntax Error - unexpected token");

        return result;
    }

    private static Node expression(Token current) {
        Node currentExpression = term(current);

        while (tokenIter.hasNext()) {
            current = tokenIter.next();

            if (current.type == Token.TokenType.IMPL) {
                currentExpression = new Node(Token.TokenType.IMPL, currentExpression, expression(tokenIter.next()));
            } else if (current.type == Token.TokenType.EQU) {
                currentExpression = new Node(Token.TokenType.EQU, currentExpression, expression(tokenIter.next()));
            } else {
                tokenIter.previous();
                break;
            }
        }
        return currentExpression;
    }

    private static Node term(Token current) {
        Node currentTerm = factor(current);

        while (tokenIter.hasNext()) {
            current = tokenIter.next();

            if (current.type == Token.TokenType.AND) {
                currentTerm = new Node(Token.TokenType.AND, currentTerm, factor(tokenIter.next()));
            } else if (current.type == Token.TokenType.OR) {
                currentTerm = new Node(Token.TokenType.OR, currentTerm, factor(tokenIter.next()));
            } else {
                tokenIter.previous();
                break;
            }
        }
        return currentTerm;
    }

    private static Node factor(Token current) {
        switch (current.type) {
            case OPEN_PARENTHESES:
                Node expr = expression(tokenIter.next());
                if (tokenIter.hasNext()) {
                    tokenIter.next();
                }
                return expr;
            case FALSE:
            case TRUE:
            case VAR:
                return new Node(current);
            case NOT:
                if (tokenIter.hasNext()) {
                    Node notExpr = factor(tokenIter.next());
                    notExpr.negate();
                    return notExpr;
                }
        }

        throw new IllegalArgumentException("Syntax Error");
    }
}
