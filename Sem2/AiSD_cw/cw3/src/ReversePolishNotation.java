import java.util.Stack;

public class ReversePolishNotation {
    public static float parse(String expr) {
        String[] split = expr.split(" ");

        Stack<Float> stack = new Stack<>();
        for (String current : split) {
            switch (current) {
                case "+":
                    stack.push(stack.pop() + stack.pop());
                    break;
                case "-":
                    stack.push(-(stack.pop() - stack.pop()));
                    break;
                case "*":
                    stack.push(stack.pop() * stack.pop());
                    break;
                case "/":
                    stack.push(1 / (stack.pop() / stack.pop()));
                    break;
                default:
                    stack.push(Float.parseFloat(current));
                    break;
            }
        }

        return stack.pop();
    }
}
