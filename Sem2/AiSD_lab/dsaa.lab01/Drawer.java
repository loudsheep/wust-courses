package dsaa.lab01;

public class Drawer {
    private static void drawLine(int n, char ch) {
        for (int i = 0; i < n; i++) {
            System.out.print(ch);
        }
    }


    public static void drawPyramid(int n) {
        drawPyramid(n, 0);
    }

    public static void drawPyramid(int n, int additional) {
        int l = 2 * n - 1;
        for (int i = 0; i < n; i++) {
            int dots = n - i - 1 + additional;
            int x = i * 2 + 1;
            drawLine(dots, '.');
            drawLine(x, 'X');
            drawLine(dots, '.');
            System.out.println();
        }
    }


    public static void drawChristmassTree(int n) {
        int l = 2 * n - 1;
        for (int i = 1; i <= n; i++) {
            int dots = n - i;
            drawPyramid(i, dots);
        }
    }

    public static void drawTriangle(int n) {
        drawLine(1, 'x');
        System.out.println();
        for (int i = 2; i < n; i++) {
            drawLine(1, 'x');
            drawLine(i - 2, '.');
            drawLine(1, 'x');
            System.out.println();
        }
        drawLine(n, 'x');
        System.out.println();
    }

}
