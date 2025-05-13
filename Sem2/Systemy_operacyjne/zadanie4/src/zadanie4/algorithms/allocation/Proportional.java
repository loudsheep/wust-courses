package zadanie4.algorithms.allocation;

import zadanie4.MemProcess;

public class Proportional extends MemoryAllocationAlgorithm {
    public Proportional(int totalFrames, MemProcess... processes) {
        super(totalFrames, processes);

        this.reassignFrames();
    }

    @Override
    protected void reassignFrames() {
        int totalPagesUsed = 0;
        for (MemProcess process : this.processes) {
            totalPagesUsed += process.getPagesUsed();
        }

        int totalAssigned = 0;

        // assign proportional frames
        for (int i = 0; i < this.processes.size(); i++) {
            MemProcess process = this.processes.get(i);
            int framesForProcess = (totalPagesUsed == 0) ? 0 :
                    (this.totalFrames * process.getPagesUsed()) / totalPagesUsed;

            this.processes.get(i).changeFrameCountInMemory(framesForProcess);
            totalAssigned += framesForProcess;
        }

        // assign leftover frames
        int leftover = this.totalFrames - totalAssigned;
        int i = 0;
        while (leftover > 0 && i < this.processes.size()) {
            this.processes.get(i).changeFrameCountInMemory(this.processes.get(i).getFramesAssigned() + 1);
            leftover--;
            i++;
        }

        System.out.println(this);
    }

    @Override
    protected void onProcessRemove() {
        this.reassignFrames();
    }
}
