
import java.util.*;

public class ACS_Main{

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        ArrayList<ACSProcess> processes = new ArrayList<>();

        System.out.print("Enter number of processes: ");
        int n = input.nextInt();

        // User input
        for (int i = 0; i < n; i++) {

            System.out.println("\nProcess P" + (i + 1));

            System.out.print("Arrival time: ");
            int arrival = input.nextInt();

            System.out.print("Burst time: ");
            int burst = input.nextInt();

            System.out.print("Priority: ");
            int priority = input.nextInt();

            processes.add(
                new ACSProcess(i + 1, arrival, burst, priority)
            );
        }

        System.out.print("\nEnter quantum: ");
        int quantum = input.nextInt();

        System.out.print("Enter context switch time: ");
        int cs = input.nextInt();

        // Sort by arrival time
        processes.sort(
            Comparator.comparingInt(p -> p.arrival)
        );

        // Reset ACS variables
        ACS.time = 0;
        ACS.next = 0;
        ACS.lastRun = null;
        ACS.tier1.clear();
        ACS.tier2.clear();

        // Scheduling output
        System.out.println("\nACS Scheduling");
        System.out.println("--------------------------------");

        System.out.printf(
            "%-8s | %-10s | %-8s%n",
            "Time", "Process", "Tier"
        );

        System.out.println("--------------------------------");

        // Run scheduling
        while (ACS.tier1.size() > 0 ||
               ACS.tier2.size() > 0 ||
               ACS.next < processes.size()) {

            ACS.addProcess(processes);

            // Tier 1 has priority
            if (!ACS.tier1.isEmpty()) {

                int start = ACS.time;
                ACSProcess p = ACS.tier1.get(0);

                if (cs != 0 && ACS.lastRun != null &&
                    ACS.lastRun != p) {

                    System.out.println(
                        "Context switch saves P" + ACS.lastRun.id +
                        " and loads P" + p.id
                    );
                }

                ACS.runtier1(processes, ACS.tier2, quantum, cs);

                System.out.printf(
                    "%-8s | %-10s | %-8s%n",
                    start + "-" + ACS.time,
                    "P" + p.id,
                    "Tier 1"
                );
            }

            // Tier 2 runs only when Tier 1 is empty
            else if (!ACS.tier2.isEmpty()) {

                int start = ACS.time;

                ACSProcess p = ACS.highestpriority(
                    ACS.tier2, ACS.time
                );

                ACS.time = ACS.runTier2(
                    ACS.tier2, ACS.time
                );

                System.out.printf(
                    "%-8s | %-10s | %-8s%n",
                    start + "-" + ACS.time,
                    "P" + p.id,
                    "Tier 2"
                );
            }

            // CPU idle
            else {
                int start = ACS.time;
                ACS.time++;
                ACS.addProcess(processes);

                System.out.printf(
                    "%-8s | %-10s | %-8s%n",
                    start + "-" + ACS.time,
                    "Idle",
                    "-"
                );
            }
        }

        // Calculate all process times
        ACS.calculateTimes(processes);

        // Print calculations
        System.out.println("\nProcess Calculations");
        System.out.println("-----------------------------------------------");

        System.out.printf(
            "%-10s | %-10s | %-12s | %-10s%n",
            "Process", "Waiting", "Turnaround", "Response"
        );

        System.out.println("-----------------------------------------------");

        for (ACSProcess p : processes) {

            System.out.printf(
                "%-10s | %-10d | %-12d | %-10d%n",
                "P" + p.id,
                p.waiting,
                p.turnaround,
                p.response
            );
        }

        System.out.println("-----------------------------------------------");

        System.out.println(
            "Average Waiting Time: " +
            ACS.averageWaitingTime(processes)
        );

        System.out.println(
            "Average Turnaround Time: " +
            ACS.averageTurnaroundTime(processes)
        );

        input.close();
    }
}
