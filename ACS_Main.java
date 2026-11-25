
import java.util.*;

public class ACS_Main{

    public static void main(String[] args){
        Scanner input=new Scanner(System.in);
        ArrayList<ACSProcess> processes=new ArrayList<>();
        System.out.print("Enter number of processes: \n");
        int n= input.nextInt();

        for(int i=0;i<n;i++){
            System.out.println("Process P"+(i+1));
            System.out.print("Arrival time: ");
            int arrival=input.nextInt();
            System.out.print("Burst time: ");
            int burst=input.nextInt();
            System.out.print("Priority: \n");
            int priority=input.nextInt();
            processes.add(new ACSProcess(i + 1, arrival, burst, priority));
        }

        System.out.print("Enter quantum: ");
        int quantum=input.nextInt();
        System.out.print("Enter context switch time: \n");
        int cs=input.nextInt();
        processes.sort(Comparator.comparingInt(p -> p.arrival));

        ACS.time=0;
        ACS.next=0;
        ACS.lastRun=null;
        ACS.tier1.clear();
        ACS.tier2.clear();

        System.out.println("ACS Scheduling");
        System.out.println("--------------------------------");
        System.out.printf(
            "%-8s | %-10s | %-8s%n",
            "Time", "Process", "Tier"
        );
        System.out.println("--------------------------------");
        input.close();
    }
}
