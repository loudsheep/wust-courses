public class Main {
    private static void execute(PageReplacementAlgorithm algorithm) {
        StatsService.reset();

        while (algorithm.hasRequestsLeft()) {
            algorithm.tick();
        }

        StatsService.showStats();
    }

    public static void main(String[] args) {
        int FRAME_COUNT = 4, PAGE_COUNT = 10;

        Page[] requests = RequestGenerator.fromStatic();

        PageReplacementAlgorithm alg1 = new FIFO(FRAME_COUNT, PAGE_COUNT, requests);
        execute(alg1);

        PageReplacementAlgorithm alg2 = new LRU(FRAME_COUNT, PAGE_COUNT, requests);
        execute(alg2);
    }
}