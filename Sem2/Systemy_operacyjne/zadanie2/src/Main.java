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
        final int SECTOR_COUNT = 1000;
        final int HEAD_INITIAL_POS = 0;

//        RequestController controller = RequestController.fromStaticTestData();
//        RequestController controller = RequestController.fromRandom(1_000_000, SECTOR_COUNT, 10_000, 0.05, 200, 400);
//        RequestController controller = RequestController.fromCSV("test_data/double_sided.csv", 0.05, 200, 400);
//        RequestController controller = RequestController.fromCSV("test_data/middle.csv", 0.05, 200, 400);
        RequestController controller = RequestController.fromCSV("test_data/after_head_cscan.csv", 0.05, 200, 400);

        StatsService.setStarvedThreshold(900);

        System.out.println("FCFS:");
        DiscScheduler fcfs = new FCFSScheduler(SECTOR_COUNT, HEAD_INITIAL_POS);
        executeSimulation(controller, fcfs);

        System.out.println("===================================");
        System.out.println("SSTF:\n");

        DiscScheduler sstf = new SSTFScheduler(SECTOR_COUNT, HEAD_INITIAL_POS);
        executeSimulation(controller, sstf);

        System.out.println("===================================");
        System.out.println("SCAN:\n");

        DiscScheduler scan = new SCANScheduler(SECTOR_COUNT);
        executeSimulation(controller, scan);

        System.out.println("===================================");
        System.out.println("C-SCAN:\n");

        DiscScheduler cscan = new CSCANScheduler(SECTOR_COUNT);
        executeSimulation(controller, cscan);


        System.out.println("===================================");
        System.out.println("SCAN/EDF:\n");

        DiscScheduler scanEdf = new SCANWithEDFScheduler(SECTOR_COUNT);
        executeSimulation(controller, scanEdf);

        System.out.println("===================================");
        System.out.println("SCAN/FD-SCAN:\n");

        DiscScheduler scanFd = new SCANWithFDSCANScheduler(SECTOR_COUNT);
        executeSimulation(controller, scanFd);
    }
}