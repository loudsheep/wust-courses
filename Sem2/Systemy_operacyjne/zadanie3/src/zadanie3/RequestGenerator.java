package zadanie3;

public class RequestGenerator {
    public static MemoryRequest[] fromStatic() {
        int[] tmp = new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};
        MemoryRequest[] requests = new MemoryRequest[tmp.length];
        for (int i = 0; i < requests.length; i++) {
            requests[i] = new MemoryRequest(i + 1, new Page(tmp[i] + ""));
        }

        return requests;
    }
}
