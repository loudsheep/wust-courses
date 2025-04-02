import java.util.ArrayList;
import java.util.List;

public class RequestController {
    private final List<DiscRequest> requests;
    private int requestIdx = 0;

    private RequestController(List<DiscRequest> requests) {
        this.requests = requests;
    }

    public void checkForNewRequest(DiscScheduler scheduler) {
        if (this.requestIdx >= this.requests.size()) return;

        while (this.hasRequestsLeft() && requests.get(requestIdx).getArrivalTime() <= scheduler.tick) {
            DiscRequest r = requests.get(requestIdx);
            r.setStatus(DiscRequest.Status.REGISTERED);

            scheduler.newRequest(r);
            requestIdx++;
        }
    }

    public boolean hasRequestsLeft() {
        return this.requestIdx < this.requests.size();
    }

    public void reset() {
        this.requestIdx = 0;
    }

    private static int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static RequestController fromStatic() {
        List<DiscRequest> list = new ArrayList<>(10);

        list.add(new DiscRequest(0, 5));
        list.add(new DiscRequest(1, 1));
        list.add(new DiscRequest(1, 20));
        list.add(new DiscRequest(2, 15));
        list.add(new DiscRequest(3, 1));
        list.add(new DiscRequest(4, 2));
        list.add(new DiscRequest(5, 1));
        list.add(new DiscRequest(5, 3));
        list.add(new DiscRequest(5, 2));
        list.add(new DiscRequest(8, 20));

        return new RequestController(list);
    }
}
