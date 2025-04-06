public class Main {

    private static void executeSimulation(RequestController controller, DiscScheduler scheduler) {
        StatsService.reset();
        controller.reset();

        long startTime = System.nanoTime();
        // Main loop
        while (scheduler.hasRequestsLeft() || controller.hasRequestsLeft()) {
            controller.checkForNewRequest(scheduler);
            scheduler.tick();
        }
        long stopTime = System.nanoTime();

        StatsService.setExecutionTime(stopTime - startTime);
        StatsService.setSimulationTicks(scheduler.tick);

        System.out.println(StatsService.getStats());
    }

    public static void main(String[] args) {
        int SECTOR_COUNT = 30;
        int HEAD_INITIAL_POS = 0;
        RequestController controller = RequestController.fromStaticTestData();


        DiscScheduler fcfs = new FCFSScheduler(SECTOR_COUNT, HEAD_INITIAL_POS);
        executeSimulation(controller, fcfs);
    }
}