import java.util.ArrayList;
import java.util.List;

public class PreemptiveSJF {
    public static class Process {
        int burst, arrival, remaining, compeltion, turnaround;
        int waiting, id;

        public Process(int id, int arrival, int burst) {
            this.id = id;
            this.arrival = arrival;
            this.burst = burst;
            this.remaining = burst;
        }
    }

    // id = -1 means CPU idle; id = -2 means context switch.
    public static class range {
        int id, start, end;
        public range(int id, int start, int end) {
            this.id = id;
            this.start = start;
            this.end = end;
        }
    }

    // Original console entry point is preserved.
    public static void SJF(List<Process> processes) {
        List<range> chart = runFromGUI(processes, 0);
        display(processes, chart);
    }

    // Runs the same preemptive-SJF selection logic and returns its real timeline.
    public static List<range> runFromGUI(List<Process> processes, int contextSwitch) {
        int current = 0;
        int completed = 0;
        Process previous = null;
        Process working = null;
        List<range> chart = new ArrayList<>();

        while (completed < processes.size()) {
            Process shortest = null;
            for (Process p : processes) {
                if (p.arrival <= current && p.remaining > 0) {
                    if (shortest == null || p.remaining < shortest.remaining) {
                        shortest = p;
                    } else if (p.remaining == shortest.remaining && p == working) {
                        shortest = p;
                    }
                }
            }

            if (shortest == null) {
                addSegment(chart, -1, current);
                current++;
                previous = null;
                working = null;
                continue;
            }

            
            if (contextSwitch > 0 && previous != null && previous != shortest) {
                addSegment(chart, -2, current, current + contextSwitch);
                current += contextSwitch;
                previous = null;
                continue;
            }

            working = shortest;
            addSegment(chart, working.id, current);
            working.remaining--;
            current++;

            if (working.remaining == 0) {
                working.compeltion = current;
                working.turnaround = working.compeltion - working.arrival;
                working.waiting = working.turnaround - working.burst;
                completed++;
            }
            previous = working;
        }
        return chart;
    }

    private static void addSegment(List<range> chart, int id, int time) {
        addSegment(chart, id, time, time + 1);
    }

    private static void addSegment(List<range> chart, int id, int start, int end) {
        if (!chart.isEmpty()) {
            range last = chart.get(chart.size() - 1);
            if (last.id == id && last.end == start) {
                last.end = end;
                return;
            }
        }
        chart.add(new range(id, start, end));
    }

    private static void display(List<Process> processes, List<range> ganttChart) {
        System.out.println("\nGantt Chart:");
        for (range s : ganttChart) {
            String name = s.id == -1 ? "IDLE" : s.id == -2 ? "CS" : "P" + s.id;
            System.out.print(name + " [" + s.start + "-" + s.end + "]  ");
        }
        System.out.println("\n\nProcess Results:");
        System.out.println("PID\tAT\tBT\tCT\tTAT\tWT");
        double totalWT = 0, totalTAT = 0;
        for (Process p : processes) {
            System.out.println("P" + p.id + "\t" + p.arrival + "\t" + p.burst + "\t" +
                    p.compeltion + "\t" + p.turnaround + "\t" + p.waiting);
            totalWT += p.waiting;
            totalTAT += p.turnaround;
        }
        if (!processes.isEmpty()) {
            System.out.println("\nAverage Waiting Time: " + totalWT / processes.size());
            System.out.println("Average Turnaround Time: " + totalTAT / processes.size());
        }
    }
}
