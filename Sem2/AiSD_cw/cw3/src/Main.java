public class Main {
    public static void main(String[] args) {
        // ZADANIE 1, 2
//        VelosoTraversableStack<Integer> vts = new VelosoTraversableStack<>();
//        vts.push(10);
//        vts.push(20);
//        vts.push(30);
//
//        System.out.println("Peek cursor: " + vts.peekCursor()); // 30
//        vts.down();
//        System.out.println("Peek cursor: " + vts.peekCursor()); // 20
//        vts.down();
//        System.out.println("Peek cursor: " + vts.peekCursor()); // 10
//
//        vts.push(40);
//
//        System.out.println(vts);
//
//        vts.reverse();
//
//        System.out.println(vts);
//
//        if (!vts.down()) {
//            System.out.println("Osiągnięto dół stosu");
//        }


        // ZADANIE 3
//        QueueWithTwoStacks<Integer> queue = new QueueWithTwoStacks<>();
//
//        queue.enqueue(1);
//        queue.enqueue(2);
//
//        System.out.println(queue.dequeue());
//
//        queue.enqueue(3);
//        queue.enqueue(4);
//        System.out.println(queue.dequeue());
//        queue.enqueue(5);
//
//        System.out.println(queue.dequeue());
//        System.out.println(queue.dequeue());
//        System.out.println(queue.dequeue());
//        System.out.println(queue.dequeue());
//        System.out.println(queue.dequeue());
//        System.out.println(queue.dequeue());
//        System.out.println(queue.dequeue());


        // ZADANIE 4
//        SinkingStackIE<Integer> stack = new SinkingStackIE<>(3);
//        stack.push(1);
//        stack.push(2);
//        stack.push(3);
//        System.out.println(stack.peek()); // 3
//        stack.push(4); // Usuwa 1, dodaje 4
//        System.out.println(stack.peek()); // 4
//        stack.push(5); // Usuwa 2, dodaje 5
//        System.out.println(stack.peek()); // 5

//        SinkingStackE<Integer> stack = new SinkingStackE<>(3);
//        stack.push(1);
//        stack.push(2);
//        stack.push(3);
//
//        stack.push(4);
//        stack.pop();
//        stack.pop();
//
//        stack.push(5);
//        stack.push(6);
//        while(!stack.isEmpty()) {
//            System.out.println("S: " + stack.pop());
//        }

    // ZADANIE 6
//        System.out.println(ReversePolishNotation.parse("5 1 - 3 + 10 * 2 /"));
    }
}