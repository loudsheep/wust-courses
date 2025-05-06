package zadanie4.algorithms.allocation;

import zadanie4.MemProcess;

public class Equal extends MemoryAllocationAlgorithm {
    public Equal(int totalFrames, MemProcess... processes) {
        super(totalFrames, processes);

        for (int i = 0; i < this.processes.length; i++) {
            int div = totalFrames / (this.processes.length - i);

            this.processes[i].changeFrameCountInMemory(div);
            totalFrames -= div;
        }
    }

    @Override
    protected void reassignFrames() {
        // After first assignment no changes need to be made
    }


}
