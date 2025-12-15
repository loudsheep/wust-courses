import java.util.Scanner;

public class Main {
    private static void tests() {
        PineTree t1 = new PineTree(10);
        System.out.println(t1);

        System.out.println("\n\n\n");

        PineTree t2 = new PineTree(-10);
        t2.applyDecoration(DecoratonFactory.getDecoration("lights"));
        System.out.println(t2);

        System.out.println("\n\n\n");

        PineTree t3 = new PineTree(8);
        t3.applyDecoration(DecoratonFactory.getDecoration("baubles"));
        t3.applyDecoration(DecoratonFactory.getDecoration("angelfefefefefefefefef"));
        System.out.println(t3);

        PineTree t4 = new PineTree(80);
        t4.applyDecoration(DecoratonFactory.getDecoration("snowflakes"));
        System.out.println(t4);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Xmas tree creator");

        int h = 0;
        while (h < 2) {
            try {
                System.out.print("Enter tree height (>=2): ");
                String input = scanner.nextLine();
                h = Integer.parseInt(input);
                if (h < 2) System.out.println("Tree must have a height of at leasy 2 !!");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number !!");
            }
        }

        ChristmasTree tree = new PineTree(h);
        System.out.println("Your raw tree:");
        System.out.println(tree);

        System.out.println();
        System.out.println("Select a decoration (BAUBLES, LIGHTS, ANGEL, SNOWFLAKES)");
        System.out.print("Enter decoration name: ");

        String decorationName = scanner.nextLine();
        try {
            IDecoration decoration = DecoratonFactory.getDecoration(decorationName);

            System.out.println("Adding decoration: " + decorationName);

            tree.applyDecoration(decoration);

            System.out.println("Tree after decoration");
            System.out.println(tree);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }

        scanner.close();
    }
}