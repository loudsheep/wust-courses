package zadanie4;

import zadanie4.algorithms.allocation.Equal;
import zadanie4.algorithms.allocation.MemoryAllocationAlgorithm;
import zadanie4.algorithms.allocation.PFF;
import zadanie4.algorithms.allocation.Proportional;
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

        RequestGenerator requests1 =
                RequestGenerator.randomWithLocality(2000, 5, 400,
                        2, 0, PAGE_COUNT);
        RequestGenerator requests2 =
                RequestGenerator.randomWithLocality(1000, 5, 400,
                        10, 0, PAGE_COUNT / 2);
        RequestGenerator requests3 =
                RequestGenerator.randomWithLocality(1500, 5, 400,
                        20, 0, PAGE_COUNT * 2);
        RequestGenerator requests4 =
                RequestGenerator.randomWithLocality(100, 5, 400,
                        10, 0, PAGE_COUNT);


        StatsService.setThrashingPeriod(10);
        StatsService.setThrashingThreshold(7);

        MemProcess p1 = new MemProcess(1, new LRU(new Memory(0), requests1));
        MemProcess p2 = new MemProcess(2, new LRU(new Memory(0), requests2));
        MemProcess p3 = new MemProcess(3, new LRU(new Memory(0), requests3));
        MemProcess p4 = new MemProcess(4, new LRU(new Memory(0), requests4));


        MemoryAllocationAlgorithm alloc1 = new Equal(TOTAL_FRAMES, p1, p2, p3, p4);
        execute(alloc1);

        p1.reset();p2.reset();p3.reset();p4.reset();
        MemoryAllocationAlgorithm alloc2 = new PFF(TOTAL_FRAMES, 0.3f, 0.6f, p1, p2, p3, p4);
        execute(alloc2);


        p1.reset();p2.reset();p3.reset();p4.reset();
        MemoryAllocationAlgorithm alloc3 = new Proportional(TOTAL_FRAMES, p1, p2, p3, p4);
        execute(alloc3);
    }
}