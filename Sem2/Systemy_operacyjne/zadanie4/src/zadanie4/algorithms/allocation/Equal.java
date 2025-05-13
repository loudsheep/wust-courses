package zadanie4.algorithms.allocation;

import zadanie4.MemProcess;

public class Equal extends MemoryAllocationAlgorithm {
    public Equal(int totalFrames, MemProcess... processes) {
        super(totalFrames, processes);

        this.reassignFrames();
    }

    @Override
    protected void reassignFrames() {
        int totalFrames = this.totalFrames;
        for (int i = 0; i < this.processes.size(); i++) {
            int div = totalFrames / (this.processes.size() - i);

            this.processes.get(i).changeFrameCountInMemory(div);
            totalFrames -= div;
        }

        System.out.println(this);
    }

    @Override
    protected void onProcessRemove() {
        this.reassignFrames();
    }
}
