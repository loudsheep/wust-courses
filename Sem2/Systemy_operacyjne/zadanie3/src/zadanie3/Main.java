package zadanie3;

import zadanie3.algorithms.allocation.MemProcess;
import zadanie3.algorithms.replacement.*;

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

        ReplacementAlgorithm alg3 = new RAND(new Memory(FRAME_COUNT), requests);
        execute(alg3);

        ReplacementAlgorithm alg4 = new OPT(new Memory(FRAME_COUNT), requests);
        execute(alg4);

//        MemProcess p1 = new MemProcess(1, new FIFO(new Memory(FRAME_COUNT), requests));
//
//        StatsService.reset();
//        while(p1.hasRequestsLeft()) {
//            p1.tick();
//        }
//
//        StatsService.showStats();
    }
}