package zadanie3.algorithms.replacement;

import zadanie3.memory.Frame;
import zadanie3.memory.Memory;
import zadanie3.MemoryRequest;
import zadanie3.StatsService;

public class LRU extends ReplacementAlgorithm {

    public LRU(Memory memory, MemoryRequest[] requests) {
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

        Frame lru = this.memory.getLRUFrame();
        this.memory.writeFrame(lru, memoryRequest.getPage(), tick);
    }
}
