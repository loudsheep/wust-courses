package zadanie3.algorithms.replacement;

import zadanie3.Frame;
import zadanie3.Memory;
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
            search.read(tick);
            return;
        }

        StatsService.pageFault();

        Frame oldest = this.memory.getOptimalFrame(this.requests, this.currentRequestIdx);
        oldest.write(memoryRequest.getPage(), tick);
    }
}
