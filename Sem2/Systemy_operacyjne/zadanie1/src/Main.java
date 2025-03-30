public class Main {

    private static void executeSimulation(ProcessController controller, ProcessHandler handler) {

        StatsService.reset();
        controller.reset();

        long startTime = System.nanoTime();
        // MAIN LOOP
        while (controller.hasProcessesLeft() || handler.hasProcessesLeft()) {
            controller.checkForNewProcess(handler);
            handler.tick();
        }
        long stopTime = System.nanoTime();

        StatsService.setSimulationTicks(handler.tick);
        StatsService.setExecutionTime(stopTime - startTime);
        System.out.println(StatsService.getStats());
    }


    public static void main(String[] args) {
        ProcessController controller = ProcessController.fromRandom(1, 100, 7_000_000, 100_000);
//        ProcessController controller = ProcessController.fromStatic();
//        ProcessController controller = ProcessController.fromCSV("data_generator/data/test.csv");


        StatsService.setStarvedThreshold(1000);

        // FCFS
        System.out.println("\n\nEXECUTING FCFS algorithm =========================");
        FCFSHandler handler = new FCFSHandler();
        executeSimulation(controller, handler);

        // SJF
        System.out.println("\n\nEXECUTING SJF algorithm =========================");
        SJFHandler handler1 = new SJFHandler();
        executeSimulation(controller, handler1);

        // RoundRobin
        System.out.println("\n\nEXECUTING RR algorithm =========================");
        RoundRobinHandler handler3 = new RoundRobinHandler(10);
        executeSimulation(controller, handler3);
    }
}