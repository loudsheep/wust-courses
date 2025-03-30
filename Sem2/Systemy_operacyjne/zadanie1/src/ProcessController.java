import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ProcessController {
    private final List<CpuProcess> processes;
    private int processIdx = 0;

    private ProcessController(List<CpuProcess> processes) {
        // assumes that already sorted
        this.processes = processes;
    }

    public void checkForNewProcess(ProcessHandler handler) {
        if (this.processIdx >= this.processes.size()) return;

        while (this.hasProcessesLeft() && processes.get(processIdx).getRegisteredAt() == handler.tick) {
            CpuProcess p = processes.get(processIdx);
            p.setRemainingTime(p.getExecutionTime());
            p.setStatus(CpuProcess.Status.REGISTERED);

            handler.registerNewProcess(p);
            processIdx++;
        }
    }

    public boolean hasProcessesLeft() {
        return this.processIdx < this.processes.size();
    }

    public void reset() {
        this.processIdx = 0;
    }

    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static ProcessController fromRandom(int minExecTime, int maxExecTime, int maxRegisterTime, int processCount) {
        List<CpuProcess> processes = new ArrayList<>(processCount);

        int totalExecTime = 0;

        for (int i = 0; i < processCount; i++) {
            int execTime = getRandomNumber(minExecTime, maxExecTime);
            int registerTime = getRandomNumber(0, maxRegisterTime);

            totalExecTime += execTime;

            CpuProcess process = new CpuProcess(execTime, registerTime);
            processes.add(process);
        }

        processes.sort(Comparator.comparingInt(CpuProcess::getRegisteredAt));

        System.out.println("Generated " + processCount + " random processes with total exec time: " + totalExecTime);
        System.out.println("First process is registered at " + processes.getFirst().getRegisteredAt());

        return new ProcessController(processes);
    }

    public static ProcessController fromCSV(String file) {
        List<CpuProcess> processes = new ArrayList<>();

        int totalExecTime = 0;

        try (Scanner scanner = new Scanner(new File(file))) {
            while (scanner.hasNextLine()) {
                String split[] = scanner.nextLine().split(",");

                if (split.length != 2) continue;

                int execTime = Integer.parseInt(split[0]);
                int registeredAt = Integer.parseInt(split[1]);

                totalExecTime += execTime;
                processes.add(new CpuProcess(execTime, registeredAt));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        processes.sort(Comparator.comparingInt(CpuProcess::getRegisteredAt));

        System.out.println("Loaded " + processes.size() + " processes from file with total exec time: " + totalExecTime);
        System.out.println("First process is registered at " + processes.getFirst().getRegisteredAt());

        return new ProcessController(processes);
    }

    public static ProcessController fromStatic() {
        List<CpuProcess> processes = new ArrayList<>(10);

        processes.add(new CpuProcess(2, 1));
        processes.add(new CpuProcess(9, 2));
        processes.add(new CpuProcess(8, 2));
        processes.add(new CpuProcess(1, 2));
        processes.add(new CpuProcess(4, 4));
        processes.add(new CpuProcess(4, 4));
        processes.add(new CpuProcess(7, 5));
        processes.add(new CpuProcess(7, 6));
        processes.add(new CpuProcess(7, 6));
        processes.add(new CpuProcess(9, 9));

        return new ProcessController(processes);
    }
}
