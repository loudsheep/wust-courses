public class Node {
    public final Token.TokenType type;
    public final Node leftOperand, rightOperand;
    public boolean nodeValue;
    public boolean negation = false;

    // Node for arithmetic operations
    public Node(Token.TokenType type, Node leftOperand, Node rightOperand) {
        this.type = type;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    // Node for storing the value
    public Node(Token token) {
        this.type = Token.TokenType.VAR;
        this.nodeValue = token.value;
        this.leftOperand = null;
        this.rightOperand = null;
    }

    public void negate() {
        this.negation = !this.negation;
    }

    public boolean isNegated() {
        return negation;
    }

    @Override
    public String toString() {
        return "Node{type=" + type +
                ", leftOperand=" + leftOperand +
                ", rightOperand=" + rightOperand +
                ", nodeValue=" + nodeValue +
                (isNegated() ? ", negated=true" : "") +
                '}';
    }
}
