package zadanie3;

import zadanie3.algorithms.replacement.*;
import zadanie3.memory.ALRUMemory;
import zadanie3.memory.Memory;

import java.util.Arrays;

public class Main {
    private static void execute(ReplacementAlgorithm algorithm) {
        StatsService.reset();

        long startTime = System.nanoTime();
        while (algorithm.hasRequestsLeft()) {
            algorithm.tick();
        }
        long stopTime = System.nanoTime();

        StatsService.setExecutionTime(stopTime - startTime);
        StatsService.showStats();
    }

    public static void main(String[] args) {
        int FRAME_COUNT = 10, PAGE_COUNT = 200;

//        MemoryRequest[] requests = RequestGenerator.fromStatic();
        MemoryRequest[] requests =
                RequestGenerator.randomWithLocality(20000, 5, 400,
                        10, 0, PAGE_COUNT);

        StatsService.setThrashingPeriod(10);
        StatsService.setThrashingThreshold(7);

        System.out.println("OPT: ");
        ReplacementAlgorithm alg4 = new OPT(new Memory(FRAME_COUNT), requests);
        execute(alg4);

        System.out.println("FIFO: ");
        ReplacementAlgorithm alg1 = new FIFO(new Memory(FRAME_COUNT), requests);
        execute(alg1);

        System.out.println("LRU: ");
        ReplacementAlgorithm alg2 = new LRU(new Memory(FRAME_COUNT), requests);
        execute(alg2);

        System.out.println("aLRU: ");
        ReplacementAlgorithm alg5 = new ALRU(new ALRUMemory(FRAME_COUNT), requests);
        execute(alg5);

        System.out.println("RAND: ");
        ReplacementAlgorithm alg3 = new RAND(new Memory(FRAME_COUNT), requests);
        execute(alg3);

    }
}