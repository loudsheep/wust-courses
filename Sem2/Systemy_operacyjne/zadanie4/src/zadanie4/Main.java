package zadanie4;

import zadanie4.algorithms.allocation.Equal;
import zadanie4.algorithms.allocation.MemoryAllocationAlgorithm;
import zadanie4.algorithms.allocation.PFF;
import zadanie4.algorithms.replacement.*;
import zadanie4.memory.Memory;

public class Main {
    private static void execute(MemoryAllocationAlgorithm algorithm) {
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
        int TOTAL_FRAMES = 16, PAGE_COUNT = 200;

        MemoryRequest[] requests1 =
                RequestGenerator.randomWithLocality(2000, 5, 400,
                        100, 0, PAGE_COUNT);
        MemoryRequest[] requests2 =
                RequestGenerator.randomWithLocality(2000, 5, 400,
                        10, 0, PAGE_COUNT / 2);
        MemoryRequest[] requests3 =
                RequestGenerator.randomWithLocality(2000, 5, 400,
                        10, 0, PAGE_COUNT * 2);
        MemoryRequest[] requests4 =
                RequestGenerator.randomWithLocality(2000, 5, 400,
                        10, 0, PAGE_COUNT);

        StatsService.setThrashingPeriod(10);
        StatsService.setThrashingThreshold(7);

        MemProcess p1 = new MemProcess(1, new LRU(new Memory(0), requests1, PAGE_COUNT));
        MemProcess p2 = new MemProcess(2, new LRU(new Memory(0), requests2, PAGE_COUNT));
        MemProcess p3 = new MemProcess(3, new LRU(new Memory(0), requests3, PAGE_COUNT));
        MemProcess p4 = new MemProcess(4, new LRU(new Memory(0), requests4, PAGE_COUNT));

        MemoryAllocationAlgorithm alloc1 = new Equal(TOTAL_FRAMES, p1, p2, p3, p4);
        System.out.println(alloc1);
        execute(alloc1);

        p1.reset();p2.reset();p3.reset();p4.reset();
        MemoryAllocationAlgorithm alloc2 = new PFF(TOTAL_FRAMES, 0.3f, 0.6f, p1, p2, p3, p4);
        System.out.println(alloc2);
        execute(alloc2);

//        System.out.println("OPT: ");
//        ReplacementAlgorithm alg4 = new OPT(new Memory(FRAME_COUNT), requests);
//        execute(alg4);
//
//        System.out.println("FIFO: ");
//        ReplacementAlgorithm alg1 = new FIFO(new Memory(FRAME_COUNT), requests);
//        execute(alg1);
//
//        System.out.println("LRU: ");
//        ReplacementAlgorithm alg2 = new LRU(new Memory(FRAME_COUNT), requests);
//        execute(alg2);
//
//        System.out.println("aLRU: ");
//        ReplacementAlgorithm alg5 = new ALRU(new ALRUMemory(FRAME_COUNT), requests);
//        execute(alg5);
//
//        System.out.println("RAND: ");
//        ReplacementAlgorithm alg3 = new RAND(new Memory(FRAME_COUNT), requests);
//        execute(alg3);

    }
}