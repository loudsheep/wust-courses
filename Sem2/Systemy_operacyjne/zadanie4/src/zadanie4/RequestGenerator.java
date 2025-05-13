package zadanie4;

import zadanie4.memory.Page;

import java.util.Random;

public class RequestGenerator {
    private final MemoryRequest[] requests;
    private int currentRequestIdx;
    private final int pagesUsed;

    private RequestGenerator(MemoryRequest[] requests, int pagesUsed) {
        this.requests = requests;
        this.currentRequestIdx = 0;
        this.pagesUsed = pagesUsed;
    }

    public boolean hasRequestsLeft() {
        return this.currentRequestIdx < this.requests.length;
    }

    public MemoryRequest nextRequest() {
        return this.requests[this.currentRequestIdx++];
    }

    public void reset() {
        this.currentRequestIdx = 0;
    }

    public int getPagesUsed() {
        return pagesUsed;
    }

    public static RequestGenerator randomWithLocality(int requestCount, int minSequenceLength, int maxSequenceLength, int sequenceRange, int requestStart, int requestEnd) {
        Random rand = new Random();
        MemoryRequest[] result = new MemoryRequest[requestCount];

        int seqenceLength = rand.nextInt(minSequenceLength, maxSequenceLength);
        int sequenceStart = rand.nextInt(requestStart, requestEnd - sequenceRange);
        for (int i = 0; i < requestCount; i++, seqenceLength--) {
            int page = rand.nextInt(sequenceStart, sequenceStart + sequenceRange);

            result[i] = new MemoryRequest(i + 1, new Page(page + ""));

            if (seqenceLength <= 0) {
                seqenceLength = rand.nextInt(minSequenceLength, maxSequenceLength);
                sequenceStart = rand.nextInt(requestStart, requestEnd - sequenceRange);
            }
        }

        return new RequestGenerator(result, requestEnd - requestStart);
    }

    public static RequestGenerator fromStatic() {
//        int[] tmp = new int[]{1, 2, 3, 4, 1, 2, 5, 3, 2, 1, 4, 5};

        int[] tmp = new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};

        MemoryRequest[] requests = new MemoryRequest[tmp.length];
        for (int i = 0; i < requests.length; i++) {
            requests[i] = new MemoryRequest(i + 1, new Page(tmp[i] + ""));
        }

        return new RequestGenerator(requests, 5);
    }
}
