package zadanie3;

public class MemoryRequest {
    private final int tick;
    private final Page page;

    public MemoryRequest(int tick, Page page) {
        this.tick = tick;
        this.page = page;
    }

    public int getTick() {
        return tick;
    }

    public Page getPage() {
        return page;
    }
}
