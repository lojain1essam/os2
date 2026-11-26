import java.util.*;

public class ACS_Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ArrayList<ACSProcess> processes = new ArrayList<>();
        System.out.print("Enter number of processes: \n");
        int n = input.nextInt();

        for (int i = 0; i < n; i++) {
            System.out.println("Process P" + (i + 1));
            System.out.print("Arrival time: ");
            int arrival = input.nextInt();
            System.out.print("Burst time: ");
            int burst = input.nextInt();
            System.out.print("Priority: \n");
            int priority = input.nextInt();
            processes.add(new ACSProcess(i + 1, arrival, burst, priority));
        }

        System.out.print("Enter quantum: ");
        int quantum = input.nextInt();
        System.out.print("Enter context switch time: \n");
        int cs = input.nextInt();
        processes.sort(Comparator.comparingInt(p -> p.arrival));

        ACS.time = 0;
        ACS.next = 0;
        ACS.lastRun = null;
        ACS.tier1.clear();
        ACS.tier2.clear();

        System.out.println("ACS Scheduling");
        System.out.println("--------------------------------");
        System.out.println("Time\tProcess\tTier");
        System.out.println("--------------------------------");

        while (ACS.next < n || ACS.tier1.size() > 0 || ACS.tier2.size() > 0) {
            ACS.addProcess(processes);
            if (ACS.tier1.size() == 0 && ACS.tier2.size() == 0) {
                System.out.println(ACS.time + "\tIdle\tTier 1: [Empty]");
                System.out.println("\t\tTier 2: [Empty]");
                ACS.time++;
            } else {
                ACSProcess p;
                if (ACS.tier1.size() > 0) {
                    p = ACS.tier1.get(0);
                } else {
                    p = ACS.highestpriority(ACS.tier2, ACS.time);
                    for (int i = 0; i < ACS.tier2.size(); i++) {
                        ACSProcess w = ACS.tier2.get(i);
                        int waited = ACS.time - w.tier2EntryTime;
                        int aging = waited / ACS.AGING_INTERVAL;
                        System.out.println("P" + w.id + ": Waited from T=" + w.tier2EntryTime + " to T=" + ACS.time +
                                " (" + waited + " units). Aging: ⌊" + waited + "/" + ACS.AGING_INTERVAL + "⌋ = " + aging +
                                ". Effective Priority = " + w.priority + " − " + aging + " = " + (w.priority - aging));
                    }
                }

                int csTime = 0;
                if (cs != 0 && ACS.lastRun != null && ACS.lastRun != p) {
                    csTime = cs;
                    if (ACS.lastRun.completed) System.out.println("Context switch loads P" + p.id);
                    else System.out.println("Context switch saves P" + ACS.lastRun.id + " and loads P" + p.id);
                }

                for (int t = 0; p.remaining > 0 && (t < csTime + quantum || p.tier2EntryTime != -1); t++) {
                    String label = t < csTime ? "CS" : "P" + p.id;
                    System.out.print(ACS.time + "\t" + label + "\tTier 1: [");
                    if (ACS.tier1.size() == 0) System.out.print("Empty");
                    for (int i = 0; i < ACS.tier1.size(); i++) {
                        ACSProcess q = ACS.tier1.get(i);
                        if (i > 0) System.out.print(", ");
                        System.out.print("P" + q.id + " (remaining: " + q.remaining + ", Priority: " + q.currentPriority + ")");
                    }
                    System.out.println("]");
                    System.out.print("\t\tTier 2: [");
                    if (ACS.tier2.size() == 0) System.out.print("Empty");
                    for (int i = 0; i < ACS.tier2.size(); i++) {
                        ACSProcess q = ACS.tier2.get(i);
                        if (i > 0) System.out.print(", ");
                        System.out.print("P" + q.id + " (remaining: " + q.remaining + ", Priority: " + q.currentPriority + ")");
                    }
                    System.out.println("]");
                    if (t == 0) {
                        ACS.tier1.remove(p);
                        ACS.tier2.remove(p);
                    }
                    if (t >= csTime) {
                        if (p.firstStart == -1) p.firstStart = ACS.time;
                        p.remaining--;
                    }
                    ACS.time++;
                    ACS.addProcess(processes);
                }

                if (p.remaining > 0) {
                    ACS.enterTier2(p, ACS.time);
                    ACS.tier2.add(p);
                } else {
                    p.completed = true;
                    p.completion = ACS.time;
                }
                ACS.lastRun = p;
            }
        }

        System.out.println(ACS.time + "\t\tTier 1: [Empty]");
        System.out.println("\t\tTier 2: [Empty]");
        System.out.println("--------------------------------");
        System.out.println();
        ACS.calculateTimes(processes);
        processes.sort(Comparator.comparingInt(p -> p.id));
        System.out.println("Process\tArrival\tFinish\tTurnaround\tWaiting\tResponse");
        for (ACSProcess p : processes) {
            System.out.println("P" + p.id + "\t" + p.arrival + "\t" + p.completion + "\t" + p.turnaround +
                    "\t\t" + p.waiting + "\t" + p.response);
        }
        System.out.println();
        System.out.println("Average Turnaround Time: " + Math.round(ACS.averageTurnaroundTime(processes) * 100) / 100.0);
        System.out.println("Average Waiting Time: " + Math.round(ACS.averageWaitingTime(processes) * 100) / 100.0);
        input.close();
    }

    // GUI adapter: runs the scheduling loop from this class and returns its timeline.
    // It receives fresh ACSProcess objects from MainFrame on every simulation.
    public static List<PreemptiveSJF.range> runFromGUI(
            List<ACSProcess> inputProcesses, int quantum, int cs) {
        if (quantum <= 0) throw new IllegalArgumentException("Time quantum must be greater than zero.");
        if (cs < 0) throw new IllegalArgumentException("Context switching cannot be negative.");
        if (inputProcesses == null || inputProcesses.isEmpty())
            throw new IllegalArgumentException("At least one process is required.");

        ArrayList<ACSProcess> processes = new ArrayList<>(inputProcesses);
        processes.sort(Comparator.comparingInt(p -> p.arrival));

        // Reset all static scheduler state so repeated GUI runs are independent.
        ACS.time = 0;
        ACS.next = 0;
        ACS.lastRun = null;
        ACS.tier1.clear();
        ACS.tier2.clear();

        ArrayList<PreemptiveSJF.range> timeline = new ArrayList<>();
        int n = processes.size();

        while (ACS.next < n || !ACS.tier1.isEmpty() || !ACS.tier2.isEmpty()) {
            ACS.addProcess(processes);

            if (ACS.tier1.isEmpty() && ACS.tier2.isEmpty()) {
                addSegment(timeline, -1, ACS.time, ACS.time + 1);
                ACS.time++;
                continue;
            }

            ACSProcess p;
            if (!ACS.tier1.isEmpty()) {
                p = ACS.tier1.get(0);
            } else {
                p = ACS.highestpriority(ACS.tier2, ACS.time);
            }

            int csTime = 0;
            if (cs != 0 && ACS.lastRun != null && ACS.lastRun != p) {
                csTime = cs;
            }

            for (int t = 0; p.remaining > 0 && (t < csTime + quantum || p.tier2EntryTime != -1); t++) {
                if (t < csTime) {
                    addSegment(timeline, -2, ACS.time, ACS.time + 1);
                } else {
                    addSegment(timeline, p.id, ACS.time, ACS.time + 1);
                }

                if (t == 0) {
                    ACS.tier1.remove(p);
                    ACS.tier2.remove(p);
                }

                if (t >= csTime) {
                    if (p.firstStart == -1) p.firstStart = ACS.time;
                    p.remaining--;
                }

                ACS.time++;
                ACS.addProcess(processes);
            }

            if (p.remaining > 0) {
                ACS.enterTier2(p, ACS.time);
                ACS.tier2.add(p);
            } else {
                p.completed = true;
                p.completion = ACS.time;
            }
            ACS.lastRun = p;
        }

        ACS.calculateTimes(processes);
        return timeline;
    }

    private static void addSegment(List<PreemptiveSJF.range> chart, int id, int start, int end) {
        if (!chart.isEmpty()) {
            PreemptiveSJF.range last = chart.get(chart.size() - 1);
            if (last.id == id && last.end == start) {
                last.end = end;
                return;
            }
        }
        chart.add(new PreemptiveSJF.range(id, start, end));
    }
}
