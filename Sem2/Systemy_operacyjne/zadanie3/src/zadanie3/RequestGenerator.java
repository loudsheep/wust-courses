package zadanie3;

import zadanie3.memory.Page;

import java.util.Random;

public class RequestGenerator {
    public static MemoryRequest[] randomWithLocality(int requestCount, int minSequenceLength, int maxSequenceLength, int sequenceRange, int requestStart, int requestEnd) {
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

        return result;
    }

    public static MemoryRequest[] fromStatic() {
//        int[] tmp = new int[]{1, 2, 3, 4, 1, 2, 5, 3, 2, 1, 4, 5};

        int[] tmp = new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};

        MemoryRequest[] requests = new MemoryRequest[tmp.length];
        for (int i = 0; i < requests.length; i++) {
            requests[i] = new MemoryRequest(i + 1, new Page(tmp[i] + ""));
        }

        return requests;
    }
}
