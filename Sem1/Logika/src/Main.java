import java.util.*;

public class Main {

    private static ArrayList<Character> getVariablesUsed(String formula) {
        ArrayList<Character> vars = new ArrayList<>();
        for (char c : formula.toCharArray()) {
            if (Character.isAlphabetic(c) && !vars.contains(c)) {
                vars.add(c);
            }
        }
        return vars;
    }

    private static HashMap<Character, Boolean> assignVariables(ArrayList<Character> vars, Scanner scanner) {
        HashMap<Character, Boolean> result = new HashMap<>();

        int i = 0;
        while (i < vars.size()) {
            char c = vars.get(i);
            System.out.print("Enter value of '" + c + "': ");
            String inp = scanner.nextLine();

            if (!Objects.equals(inp, "0") && !Objects.equals(inp, "1")) {
                System.out.println("Incorrect value: " + inp);
                continue;
            }

            boolean value = !inp.equals("0");
            result.put(c, value);

            i++;
        }

        return result;
    }

    private static HashMap<Character, Boolean> assignRandomVariables(ArrayList<Character> vars) {
        HashMap<Character, Boolean> result = new HashMap<>();
        for (char c : vars) {
            result.put(c, false);
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter formula: ");
        String formula = scan.nextLine();

        // Evaluate formula
        HashMap<Character, Boolean> vars = Main.assignVariables(Main.getVariablesUsed(formula), scan);

        ArrayList<Token> tokens = Token.tokenize(formula, vars);
        Node expressionTree = Parser.parseTokens(tokens);

        boolean result = Interpreter.getResult(expressionTree);
        System.out.println("Result: " + (result ? "true" : "false"));

        System.out.println();
        System.out.println("Generated Truth Table");

        TruthTable.printVarsInTable(vars, true, formula);
        TruthTable.generateTruthTable(new HashMap<>(), vars, formula);
    }

}