package zadanie4.algorithms.replacement;

import zadanie4.RequestGenerator;
import zadanie4.memory.Frame;
import zadanie4.memory.Memory;
import zadanie4.MemoryRequest;
import zadanie4.StatsService;

public class LRU extends ReplacementAlgorithm {

    public LRU(Memory memory, RequestGenerator requests) {
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

        Frame lru = this.memory.getLRUFrame();
        this.memory.writeFrame(lru, memoryRequest.getPage(), tick);
    }
}
