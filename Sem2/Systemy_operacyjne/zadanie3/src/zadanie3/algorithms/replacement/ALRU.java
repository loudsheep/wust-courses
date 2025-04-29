package zadanie3.algorithms.replacement;

import zadanie3.StatsService;
import zadanie3.memory.ALRUMemory;
import zadanie3.MemoryRequest;
import zadanie3.memory.Frame;

public class ALRU extends ReplacementAlgorithm {
    public ALRU(ALRUMemory memory, MemoryRequest[] requests) {
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

        StatsService.pageFault();

        Frame lru = ((ALRUMemory) this.memory).getALRUFrame();
        this.memory.writeFrame(lru, memoryRequest.getPage(), tick);
    }
}
