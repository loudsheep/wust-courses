package zadanie3;

import zadanie3.algorithms.replacement.*;
import zadanie3.memory.ALRUMemory;
import zadanie3.memory.Memory;

public class Main {
    private static void execute(ReplacementAlgorithm algorithm) {
        StatsService.reset();

        while (algorithm.hasRequestsLeft()) {
            algorithm.tick();
        }

        StatsService.showStats();
    }

    public static void main(String[] args) {
        int FRAME_COUNT = 4, PAGE_COUNT = 10;

        MemoryRequest[] requests = RequestGenerator.fromStatic();

        System.out.println("FIFO: ");
        ReplacementAlgorithm alg1 = new FIFO(new Memory(FRAME_COUNT), requests);
        execute(alg1);

        System.out.println("LRU: ");
        ReplacementAlgorithm alg2 = new LRU(new Memory(FRAME_COUNT), requests);
        execute(alg2);

        System.out.println("RAND: ");
        ReplacementAlgorithm alg3 = new RAND(new Memory(FRAME_COUNT), requests);
        execute(alg3);

        System.out.println("OPT: ");
        ReplacementAlgorithm alg4 = new OPT(new Memory(FRAME_COUNT), requests);
        execute(alg4);

        System.out.println("aLRU: ");
        ReplacementAlgorithm alg5 = new ALRU(new ALRUMemory(FRAME_COUNT), requests);
        execute(alg5);
    }
}