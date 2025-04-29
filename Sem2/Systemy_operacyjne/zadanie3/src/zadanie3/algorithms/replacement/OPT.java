package zadanie3.algorithms.replacement;

import zadanie3.memory.Frame;
import zadanie3.memory.Memory;
import zadanie3.MemoryRequest;
import zadanie3.StatsService;

public class OPT extends ReplacementAlgorithm {

    public OPT(Memory memory, MemoryRequest[] requests) {
        super(memory, requests);
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
