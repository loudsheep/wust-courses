package zadanie3;

import zadanie3.algorithms.replacement.FIFO;
import zadanie3.algorithms.replacement.LRU;
import zadanie3.algorithms.replacement.ReplacementAlgorithm;

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

        ReplacementAlgorithm alg1 = new FIFO(new Memory(FRAME_COUNT), requests);
        execute(alg1);

        ReplacementAlgorithm alg2 = new LRU(new Memory(FRAME_COUNT), requests);
        execute(alg2);
    }
}