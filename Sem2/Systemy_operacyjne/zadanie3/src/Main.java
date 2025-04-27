public class Main {
    private static void execute(PageReplacementAlgorithm algorithm, RequestController controller) {
        controller.reset();
        StatsService.reset();

        while (controller.hasRequestsLeft() || algorithm.hasRequestsLeft()) {
            controller.getNextRequest(algorithm);
            algorithm.tick();
        }

        StatsService.showStats();
    }

    public static void main(String[] args) {
        int FRAME_COUNT = 4, PAGE_COUNT = 10;

        RequestController controller = RequestController.fromStatic();

        PageReplacementAlgorithm alg1 = new FIFO(FRAME_COUNT, PAGE_COUNT);
        execute(alg1, controller);

        PageReplacementAlgorithm alg2 = new LRU(FRAME_COUNT, PAGE_COUNT);
        execute(alg2, controller);
    }
}