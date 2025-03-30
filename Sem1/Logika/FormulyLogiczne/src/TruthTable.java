import java.util.ArrayList;
import java.util.HashMap;

public class TruthTable {
    public static void printVarsInTable(HashMap<Character, Boolean> vars, boolean header, String formula) {
        if (header) {
            for (char c: vars.keySet()) System.out.print(c + " ");
            System.out.println(formula);
            return;
        }

        for (boolean b: vars.values()) {
            System.out.print(b ? "1 ": "0 ");
        }
    }

    public static void generateTruthTable(HashMap<Character, Boolean> combination, HashMap<Character, Boolean> varsToUse, String formula) {
        if (varsToUse.isEmpty()) {
            ArrayList<Token> tokens = Token.tokenize(formula, combination);
            Node expressionTree = Parser.parseTokens(tokens);

            printVarsInTable(combination, false, formula);
            boolean result = Interpreter.getResult(expressionTree);
            System.out.println(result ? "1": "0");
            return;
        }

        char var = (char) varsToUse.keySet().toArray()[0];

        combination.put(var, false);
        varsToUse.remove(var);
        generateTruthTable(combination, varsToUse, formula);

        combination.remove(var);
        combination.put(var, true);
        generateTruthTable(combination, varsToUse, formula);

        combination.remove(var);
        varsToUse.put(var, true);
    }

}
