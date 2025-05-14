package zadanie4.algorithms.allocation;

import zadanie4.MemProcess;

public class PFF extends MemoryAllocationAlgorithm {
    private final float lowerPffBound;
    private final float higherPffBound;

    public PFF(int totalFrames, float lowerPffBound, float higherPffBound, MemProcess... processes) {
        super(totalFrames, processes);

        this.lowerPffBound = lowerPffBound;
        this.higherPffBound = higherPffBound;

        // initial equal assignment
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
        boolean changed = false;

        // Step 1: Give 1 frame to paused/starved processes (if any frames are free)
        for (MemProcess process : this.processes) {
            if (process.getFramesAssigned() == 0 && this.freeFrames > 0) {
                process.changeFrameCountInMemory(1);
                this.freeFrames--;
                process.getMemory().resetPffRatio();  // Allow it to start fresh
                changed = true;
            }
        }

        // Step 2: Take frames from processes with low PFF ratio
        for (MemProcess process : this.processes) {
            int assigned = process.getFramesAssigned();
            float pff = process.getMemory().getPffRatio();

            if (assigned > 0 && pff < this.lowerPffBound) {
                process.changeFrameCountInMemory(assigned - 1);
                this.freeFrames++;
                process.getMemory().resetPffRatio();
                changed = true;
            }
        }

        // Step 3: Give frames to high-PFF processes if free frames exist
        for (MemProcess process : this.processes) {
            float pff = process.getMemory().getPffRatio();

            if (pff > this.higherPffBound && this.freeFrames > 0) {
                process.changeFrameCountInMemory(process.getFramesAssigned() + 1);
                this.freeFrames--;
                process.getMemory().resetPffRatio();
                changed = true;
            }
        }

        // Step 4: Distribute any remaining frames equally (optional)
        distributeLeftOvers();

        if (changed) {
            this.onFrameAllocChange();
        }
    }

    @Override
    protected void onProcessRemove() {
        distributeLeftOvers();
    }

    private void distributeLeftOvers() {
        int i = 0;
        while (this.freeFrames > 0 && i < this.processes.size()) {
            this.processes.get(i).changeFrameCountInMemory(this.processes.get(i).getFramesAssigned() + 1);
            this.freeFrames--;
            i++;
        }
    }
}
