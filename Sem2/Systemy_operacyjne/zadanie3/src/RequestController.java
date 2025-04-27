public class RequestController {
    private final int[] requests;
    private int currentRequest;

    private RequestController(int[] requests) {
        this.requests = requests;
        this.currentRequest = 0;
    }

    public void getNextRequest(PageReplacementAlgorithm algorithm) {
        if (!this.hasRequestsLeft()) return;

        algorithm.newRequest(requests[currentRequest++]);
    }

    public boolean hasRequestsLeft() {
        return this.currentRequest < this.requests.length;
    }

    public void reset() {
        this.currentRequest = 0;
    }

    public static RequestController fromStatic() {
        int[] requests = new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};

        return new RequestController(requests);
    }
}
