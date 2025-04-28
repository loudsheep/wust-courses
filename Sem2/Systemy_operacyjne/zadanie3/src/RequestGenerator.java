public class RequestGenerator {
    public static Page[] fromStatic() {
        int[] tmp = new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};
        Page[] requests = new Page[tmp.length];
        for (int i = 0; i < requests.length; i++) {
            requests[i] = new Page(tmp[i] + "");
        }

        return requests;
    }
}
