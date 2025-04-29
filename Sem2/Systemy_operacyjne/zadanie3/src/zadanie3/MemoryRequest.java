package zadanie3;

import zadanie3.memory.Page;

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

    @Override
    public String toString() {
        return page.toString();
    }
}
