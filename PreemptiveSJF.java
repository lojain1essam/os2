import java.util.ArrayList;
import java.util.List;
import java.util.*;

import javax.swing.text.Segment;

public class PreemptiveSJF {
    static class Process{
        int burst , arrival, remaining , compeltion, turnaround;
        int waiting , id;

        Process(int id , int arrival , int burst){
            this.id = id;
            this.arrival = arrival;
            this.burst = burst;
            this.remaining = burst;
        }
        
    }

    static class range{
        int id , start , end;
        range(int id , int start , int end){
            this.id = id;
            this.start = start;
            this.end = end;
        }
    }

    public static void SJF(List <Process> processes){
        int current = 0, completed = 0;
        Process working = null;

        List <range> grant = new ArrayList<>();
        
        while(completed < processes.size()){
            Process Short = null;
            for(Process p : processes){
                if(p.arrival <= current && p.remaining > 0){

                    if(Short == null || p.remaining < Short.remaining){
                        Short = p;
                    }

                    else if(Short.remaining == p.remaining){
                        if(p == working){
                            Short = p;
                        }
                    }
                }
            }
            if (Short == null) {
                 current++;
                continue;
            }   
            working = Short;
            addSegment(grant , working.id , current);

            working.remaining--;
            current++;

            if (working.remaining == 0) {
                working.compeltion = current;

                working.turnaround = working.compeltion - working.arrival;

                working.waiting = working.turnaround - working.burst;

                completed++;
                working = null;
            }
        }

        display(processes , grant);
    }
    
    private static void addSegment(List<range> chart, int id, int time){

        if (!chart.isEmpty()) {

            range last = chart.get(chart.size()-1);

            if (last.id == id) {
                last.end = time + 1;
                return;
            }
        }

        chart.add(new range(id, time, time + 1));
    }

     private static void display(
        List<Process> processes,
        List<range> ganttChart) {

        System.out.println("\nGantt Chart:");

        for (range s : ganttChart) {
            String name = s.id == -1 ? "IDLE" : "P" + s.id;

            System.out.print(
                name + " [" + s.start + "-" + s.end + "]  "
            );
        }

        System.out.println("\n\nProcess Results:");

        System.out.println(
            "PID\tAT\tBT\tCT\tTAT\tWT"
        );

        double totalWT = 0;
        double totalTAT = 0;

        for (Process p : processes) {

            System.out.println(
                "P" + p.id + "\t" +
                p.arrival + "\t" +
                p.burst + "\t" +
                p.compeltion + "\t" +
                p.turnaround + "\t" +
                p.waiting
            );

            totalWT += p.waiting;
            totalTAT += p.turnaround;
        }

        System.out.println("\nAverage Waiting Time: " +
            totalWT / processes.size());

        System.out.println("Average Turnaround Time: " +
            totalTAT / processes.size());
    }

}
