package zadanie4.algorithms.allocation;

import zadanie4.MemProcess;

import java.util.*;

public class WorkingSetModel extends MemoryAllocationAlgorithm {
    private final int deltaT;
    private final int updateFrequency; // c < deltaT
    private int tickCount = 0;

    public WorkingSetModel(int totalFrames, int deltaT, int updateFrequency, MemProcess... processes) {
        super(totalFrames, processes);
        this.deltaT = deltaT;
        this.updateFrequency = updateFrequency;

        // initial equal alloc
        int frames = this.totalFrames;
        for (int i = 0; i < this.processes.size(); i++) {
            int div = frames / (this.processes.size() - i);

            this.processes.get(i).changeFrameCountInMemory(div);
            frames -= div;
        }
    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;

        if (tickCount % updateFrequency == 0) {
            reassignFrames();
        }
    }

    @Override
    protected void reassignFrames() {
        Map<MemProcess, Integer> wssMap = new HashMap<>();
        int totalWSS = 0;

        for (MemProcess process : this.processes) {
            if (process.isPaused()) continue;

            int wss = process.getMemory().getWorkingSetSize(deltaT);  // You must implement this method
            wssMap.put(process, wss);
            totalWSS += wss;
        }

        if (totalWSS <= this.totalFrames) {
            // we can assign WSS to all active processes
            this.freeFrames = this.totalFrames;

            for (Map.Entry<MemProcess, Integer> entry : wssMap.entrySet()) {
                MemProcess p = entry.getKey();
                int wss = entry.getValue();
                p.changeFrameCountInMemory(wss);
                this.freeFrames -= wss;
            }

            // resume suspended processes if space is available
            resumeSuspendedIfPossible();
        } else {
            // not enough frames -> suspend a process
            MemProcess toSuspend = Collections.max(wssMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
            int framesFreed = toSuspend.getFramesAssigned();
            toSuspend.changeFrameCountInMemory(0);
            this.freeFrames += framesFreed;
        }
    }

    private void resumeSuspendedIfPossible() {

        for (MemProcess process : this.processes) {
            if (!process.isPaused()) continue;

            int wss = process.getMemory().getWorkingSetSize(deltaT);

            if (wss <= this.freeFrames) {
                process.changeFrameCountInMemory(wss);
                this.freeFrames -= wss;
            }
        }
    }

    @Override
    protected void onProcessRemove() {
        reassignFrames();
    }
}
