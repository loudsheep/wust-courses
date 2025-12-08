public class Main {
    private static void printRecursive(Object[] args, int index) {
        if (index >= args.length) {
            return;
        }

        Object obj = args[index];
        String typeName = obj != null ? obj.getClass().getSimpleName() : "null";

        System.out.println(typeName + ": " + obj);
        printRecursive(args, index + 1);
    }

    public static void printAll(Object... args) {
        printRecursive(args, 0);
    }

    public static void main(String[] args) {
        System.out.println("------------------------");
        printAll(1, "hello", 3.14);

        System.out.println("------------------------");
        printAll(true, 'Z', 100);

        System.out.println("------------------------");
        printAll();

        System.out.println("------------------------");
        printAll(1.1, 2.2f, 3L, (short) 4);

        System.out.println("------------------------");
        printAll(new int[]{1, 2, 4}, null);
    }
}