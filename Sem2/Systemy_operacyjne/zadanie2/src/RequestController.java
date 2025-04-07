import java.util.ArrayList;
import java.util.Comparator;
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

            if (r.isRealTime()) scheduler.newRealTimeRequest(r);
            else scheduler.newRequest(r);
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

    public static RequestController fromRandom(int maxArrivalTime, int maxSector, int count, double realTimeProbability) {
        List<DiscRequest> requests = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int arrivalTime = getRandomInt(0, maxArrivalTime);
            int sector = getRandomInt(0, maxSector);
            boolean realTime = Math.random() < realTimeProbability;

            requests.add(new DiscRequest(arrivalTime, sector, realTime));
        }

        requests.sort(Comparator.comparingInt(DiscRequest::getArrivalTime));

        return new RequestController(requests);
    }

    public static RequestController fromStaticTestData() {
        List<DiscRequest> list = new ArrayList<>(10);

        list.add(new DiscRequest(0, 8));
        list.add(new DiscRequest(1, 4));
        list.add(new DiscRequest(1, 4));
        list.add(new DiscRequest(1, 28));
        list.add(new DiscRequest(1, 24));
        list.add(new DiscRequest(1, 22));
        list.add(new DiscRequest(1, 3));
        list.add(new DiscRequest(1, 2));
        list.add(new DiscRequest(1, 14));
        list.add(new DiscRequest(1, 28));
//        list.add(new DiscRequest(10, 0));
//        list.add(new DiscRequest(4000, 2500));

//        list.add(new DiscRequest(0, 5));
//        list.add(new DiscRequest(1, 1));
//        list.add(new DiscRequest(1, 20));
//        list.add(new DiscRequest(2, 15));
//        list.add(new DiscRequest(3, 1));
//        list.add(new DiscRequest(4, 2));
//        list.add(new DiscRequest(5, 1));
//        list.add(new DiscRequest(5, 3));
//        list.add(new DiscRequest(5, 2));
//        list.add(new DiscRequest(8, 20));

        return new RequestController(list);
    }
}
