public class Interpreter {
    public static boolean getResult(Node node) {
        boolean tmp;
        switch (node.type) {
            case AND -> tmp = getResult(node.leftOperand) & getResult(node.rightOperand);
            case OR -> tmp = getResult(node.leftOperand) | getResult(node.rightOperand);
            case IMPL -> tmp = !getResult(node.leftOperand) | getResult(node.rightOperand);
            case EQU -> tmp = getResult(node.leftOperand) == getResult(node.rightOperand);
            case FALSE -> tmp = false;
            case TRUE -> tmp = true;
            case VAR -> tmp = node.nodeValue;
            default -> throw new InternalError("Unknown Error Encountered");
        }

        if (node.isNegated()) {
            return !tmp;
        }
        return tmp;
    }
}
