package zadanie4.algorithms.replacement;

import zadanie4.memory.Frame;
import zadanie4.memory.Memory;
import zadanie4.MemoryRequest;
import zadanie4.StatsService;

public class OPT extends ReplacementAlgorithm {

    public OPT(Memory memory, MemoryRequest[] requests, int pagesUsed) {
        super(memory, requests, pagesUsed);
    }

    @Override
    public void tick() {
        super.tick();

        MemoryRequest memoryRequest = this.nextRequest();

        Frame search = this.memory.searchForPage(memoryRequest.getPage());

        if (search != null) {
            this.memory.readFrame(search, tick);
            return;
        }

        Frame optimal = this.memory.getOptimalFrame(this.requests, this.currentRequestIdx);
        this.memory.writeFrame(optimal, memoryRequest.getPage(), tick);
    }
}
