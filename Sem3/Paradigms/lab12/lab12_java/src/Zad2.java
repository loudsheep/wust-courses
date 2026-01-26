import java.util.Random;
import java.util.concurrent.Semaphore;

public class Zad2 {
    static final int N = 5;
    static int[] buffer = new int[N];
    static int in = 0, out = 0;

    static Semaphore empty = new Semaphore(N);
    static Semaphore full = new Semaphore(0);
    static Semaphore mutex = new Semaphore(1);

    static class Producer extends Thread {
        int id;

        Producer(int id) {
            this.id = id;
        }

        Random rand = new Random();

        public void run() {
            try {
                while (true) {
                    int x = rand.nextInt(500, 5000);
                    empty.acquire();

                    mutex.acquire();
                    buffer[in] = x;
                    System.out.println("Producent " + id + " wyprodukował " + x);
                    in = (in + 1) % N;
                    mutex.release();

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
                    mutex.acquire();

                    int x = buffer[out];
                    out = (out + 1) % N;

                    mutex.release();
                    empty.release();

                    System.out.println("Konsument " + id + " rozpoczął " + x);
                    Thread.sleep(x);
                    System.out.println("Konsument " + id + " zakończył " + x);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        int producerCount = 2;
        int consumerCount = 5;

        for (int i = 1; i <= producerCount; i++) {
            new Producer(i).start();
        }
        for (int i = 1; i <= consumerCount; i++) {
            new Consumer(i).start();
        }
    }
}
