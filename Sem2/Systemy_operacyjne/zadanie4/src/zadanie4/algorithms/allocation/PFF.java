package zadanie4.algorithms.allocation;

import zadanie4.MemProcess;

public class PFF extends MemoryAllocationAlgorithm {
    private final float lowerPffBound;
    private final float higherPffBound;

    public PFF(int totalFrames, float lowerPffBound, float higherPffBound, MemProcess... processes) {
        super(totalFrames, processes);

        this.lowerPffBound = lowerPffBound;
        this.higherPffBound = higherPffBound;

        for (int i = 0; i < this.processes.size(); i++) {
            int div = totalFrames / (this.processes.size() - i);

            this.processes.get(i).changeFrameCountInMemory(div);
            totalFrames -= div;
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.reassignFrames();
    }

    @Override
    protected void reassignFrames() {
        // TODO: sort processes, and assign leftovers after

        boolean c = false;
        for (MemProcess process : this.processes) {
            int assigned = process.getFramesAssigned();
            float pff = process.getMemory().getPffRatio();

            // Process is doing good - take one frame
            if (pff < this.lowerPffBound && assigned > 0) {
                process.changeFrameCountInMemory(assigned - 1);
                process.getMemory().resetPffRatio();

                this.freeFrames++;
                c = true;
            }
        }

        for (MemProcess process : this.processes) {
            int assigned = process.getFramesAssigned();
            float pff = process.getMemory().getPffRatio();

            // Process is doing good - take one frame
            if (pff > this.higherPffBound && this.freeFrames > 0) {
                process.changeFrameCountInMemory(assigned + 1);
                process.getMemory().resetPffRatio();

                this.freeFrames--;
                c = true;
            }
        }

        int i = 0;
        while (this.freeFrames > 0 && i < this.processes.size()) {
            this.processes.get(i).changeFrameCountInMemory(this.processes.get(i).getFramesAssigned() + 1);
            this.freeFrames--;
            i++;
        }

        if (c) {
            System.out.println(this);
        }
    }

    @Override
    protected void onProcessRemove() {
        int i = 0;
        while (this.freeFrames > 0 && i < this.processes.size()) {
            this.processes.get(i).changeFrameCountInMemory(this.processes.get(i).getFramesAssigned() + 1);
            this.freeFrames--;
            i++;
        }
    }
}
