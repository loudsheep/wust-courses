public class Main {

    private static void executeSimulation(RequestController controller, DiscScheduler scheduler) {
        StatsService.reset();
        controller.reset();
int i = 0;
        long startTime = System.nanoTime();
        // Main loop
        while (scheduler.hasRequestsLeft() || controller.hasRequestsLeft()) {
            controller.checkForNewRequest(scheduler);
            scheduler.tick();
            i++;
//            if (i > 30) break;;
        }
        long stopTime = System.nanoTime();

        StatsService.setExecutionTime(stopTime - startTime);
        StatsService.setSimulationTicks(scheduler.tick);

        System.out.println(StatsService.getStats());
    }

    public static void main(String[] args) {
        int SECTOR_COUNT = 3000;
        int HEAD_INITIAL_POS = 0;
//        RequestController controller = RequestController.fromStaticTestData();
        RequestController controller = RequestController.fromRandom(10000, SECTOR_COUNT, 1000, -1);


        System.out.println("FCFS:");
        DiscScheduler fcfs = new FCFSScheduler(SECTOR_COUNT, HEAD_INITIAL_POS);
        executeSimulation(controller, fcfs);


        System.out.println("===================================");
        System.out.println("SSTF:\n");

        DiscScheduler sstf = new SSTFScheduler(SECTOR_COUNT, HEAD_INITIAL_POS);
        executeSimulation(controller, sstf);
    }
}