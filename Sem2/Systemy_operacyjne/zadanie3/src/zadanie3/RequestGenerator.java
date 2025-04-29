package zadanie3;

import zadanie3.memory.Page;

public class RequestGenerator {
    public static MemoryRequest[] randomWithLocality(int count) {
        return new MemoryRequest[0];
    }

    public static MemoryRequest[] fromStatic() {
        int[] tmp = new int[]{1, 2, 3, 4, 1, 2, 5, 3, 2, 1, 4, 5};

//        int[] tmp = new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};

        MemoryRequest[] requests = new MemoryRequest[tmp.length];
        for (int i = 0; i < requests.length; i++) {
            requests[i] = new MemoryRequest(i + 1, new Page(tmp[i] + ""));
        }

        return requests;
    }
}
