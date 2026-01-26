import java.util.Random;
import java.util.concurrent.Semaphore;

public class Zad1 {
    static int buffer;
    static Semaphore empty = new Semaphore(1);
    static Semaphore full = new Semaphore(0);

    static class Producer extends Thread {
        public void run() {
            Random rand = new Random();
            try {
                while (true) {
                    int x = rand.nextInt(500, 5000);
                    empty.acquire();
                    buffer = x;
                    full.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Consumer extends Thread {
        int id;

        Consumer(int id) {
            this.id = id;
        }

        public void run() {
            try {
                while (true) {
                    full.acquire();
                    int x = buffer;
                    empty.release();

                    System.out.println("Konsument " + id + " rozpoczął " + x); // [cite: 11]
                    Thread.sleep(x);
                    System.out.println("Konsument " + id + " zakończył " + x); // [cite: 13]
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        int numConsumers = 5;

        new Producer().start();
        for (int i = 1; i <= numConsumers; i++) {
            new Consumer(i).start();
        }
    }
}