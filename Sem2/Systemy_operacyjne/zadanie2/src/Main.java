public class Main {
    public static void main(String[] args) {
        int SECTOR_COUNT = 1000;
        int HEAD_INITIAL_POS = 0;
        RequestController controller = RequestController.fromStatic();

        DiscScheduler scheduler = new FCFSScheduler(SECTOR_COUNT, HEAD_INITIAL_POS);

        while (scheduler.hasRequestsLeft() || controller.hasRequestsLeft()) {
            controller.checkForNewRequest(scheduler);
            scheduler.tick();
        }

        System.out.println("DONE");
    }
}