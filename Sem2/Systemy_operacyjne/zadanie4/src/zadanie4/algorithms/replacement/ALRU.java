package zadanie4.algorithms.replacement;

import zadanie4.StatsService;
import zadanie4.memory.ALRUMemory;
import zadanie4.MemoryRequest;
import zadanie4.memory.Frame;

public class ALRU extends ReplacementAlgorithm {
    public ALRU(ALRUMemory memory, MemoryRequest[] requests, int pagesUsed) {
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

        Frame lru = ((ALRUMemory) this.memory).getALRUFrame();
        this.memory.writeFrame(lru, memoryRequest.getPage(), tick);
    }
}
