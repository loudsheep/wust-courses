package zadanie4;

import zadanie4.algorithms.allocation.*;
import zadanie4.algorithms.replacement.*;
import zadanie4.memory.Memory;

import java.util.Random;

public class Main {
    private static void resetProcesses(MemProcess... processes) {
        for (MemProcess process : processes) {
            process.reset();
        }
    }

    private static MemProcess generateRandomProcess(int reqestsOffset, int pid) {
        int MAX_REQUESTS = 4000;
        int MIN_SEQUENCE_LEN = 5;
        int MAX_SEQUENCE_LEN = 400;
        int MIN_SEQUENCE_RANGE = 2;
        int MAX_SEQUENCE_RANGE = 10;
        int MAX_PAGES_LEN = 400;
        Random random = new Random();

        RequestGenerator generator = RequestGenerator.randomWithLocality(random.nextInt(MAX_REQUESTS), MIN_SEQUENCE_LEN, MAX_SEQUENCE_LEN, random.nextInt(MIN_SEQUENCE_RANGE, MAX_SEQUENCE_RANGE), 0, random.nextInt(MAX_PAGES_LEN));

        return new MemProcess(pid, new LRU(new Memory(0), generator));
    }

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
        int NUM_PROCESSES = 10;

        MemProcess[] processes = new MemProcess[NUM_PROCESSES];
        for (int i = 0; i < NUM_PROCESSES; i++) {
            processes[i] = generateRandomProcess(PAGE_COUNT * i, i + 1);
        }

        StatsService.setThrashingPeriod(10);
        StatsService.setThrashingThreshold(7);

        MemoryAllocationAlgorithm alloc1 = new Equal(TOTAL_FRAMES, processes);
        execute(alloc1);

        resetProcesses(processes);
        MemoryAllocationAlgorithm alloc2 = new PFF(TOTAL_FRAMES, 0.3f, 0.6f, processes);
        execute(alloc2);


        resetProcesses(processes);
        MemoryAllocationAlgorithm alloc3 = new Proportional(TOTAL_FRAMES, processes);
        execute(alloc3);

        resetProcesses(processes);
        MemoryAllocationAlgorithm alloc4 = new WorkingSetModel(TOTAL_FRAMES, 10, 5, processes);
        execute(alloc4);
    }
}