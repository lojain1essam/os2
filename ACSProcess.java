public class ACSProcess {
    int id, arrival, burst, remaining, priority, currentPriority, completion, turnaround, waiting;
    int firstStart=-1, response= -1;
    boolean completed=false;
    int tier2EntryTime = -1;

    ACSProcess(int id, int arrival, int burst, int priority){
        this.id = id;
        this.arrival = arrival;
        this.burst = burst;
        this.remaining = burst;
        this.priority = priority;
        this.currentPriority = priority;
    }
}
