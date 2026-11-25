import java.util.ArrayList;

public class ACS {
    public static int time=0, next=0;
    public static ACSProcess lastRun=null;
    public static ArrayList<ACSProcess> tier1=new ArrayList<>();
    public static ArrayList<ACSProcess> tier2=new ArrayList<>();
    public ArrayList<ACSProcess> processes;
    public int quantum;
    public int cs;
    static final int AGING_INTERVAL = 4;
    static String lastState = "";

    //tier1
    public static void addProcess(ArrayList<ACSProcess> processes){
        while (next<processes.size() && processes.get(next).arrival<=time){
            tier1.add(processes.get(next));
            next++;
        }
    }

    public static void runtier1(ArrayList<ACSProcess> processes, ArrayList<ACSProcess> tier2, int quantum, int cs){
        if (tier1.isEmpty()){
            return;
        }
        ACSProcess p=tier1.get(0);
       
       if(cs!=0 && lastRun!=null && lastRun!=p){
            time += cs;
            addProcess(processes);
        }
        tier1.remove(0);
        
        if(p.firstStart==-1)p.firstStart = time;

        int used = 0;
        while(used<quantum && p.remaining>0){
            p.remaining--;
            time++;
            used++;
            addProcess(processes);
        }

        if(p.remaining>0){
           enterTier2(p, time);
           tier2.add(p);
         }
        else {p.completed= true;
             p.completion = time;}
        lastRun= p;
    }

    //tier 2
     public static ACSProcess highestpriority(
        ArrayList<ACSProcess> tier2, int time){

      applyAging(tier2, time);

      ACSProcess highest = tier2.get(0);

      for (int i = 0; i < tier2.size(); i++) {
         ACSProcess p = tier2.get(i);
         if (p.currentPriority < highest.currentPriority)
            highest = p;
        }
        return highest;
    }
    
    public static int runTier2(ArrayList<ACSProcess>tier2,int time){
        if(tier2.isEmpty())return time;
        ACSProcess p = highestpriority(tier2, time); 
        if(p.firstStart==-1)p.firstStart = time;
        time+=p.remaining;
        p.remaining=0;
        p.completed=true;
        p.completion=time;
       // p.turnaround=p.completion-p.arrival;
       // p.waiting=p.turnaround-p.burst;
        tier2.remove(p);

        return time;
    }

    //aging
    public static void enterTier2(
            ACSProcess p, int currentTime) {

        p.tier2EntryTime = currentTime;
        p.currentPriority = p.priority;
    }

    public static int calculateEffectivePriority(
            ACSProcess p, int currentTime) {

        int waitingTime =
            currentTime - p.tier2EntryTime;

        int agingAmount =
            waitingTime / AGING_INTERVAL;

        return p.priority - agingAmount;
    }

    
    public static void applyAging(
            ArrayList<ACSProcess> tier2, int currentTime) {

        for (ACSProcess p : tier2) {

            p.currentPriority =
                calculateEffectivePriority(p, currentTime);
        }
    }

    //calculations
    public static void calculateTimes(ArrayList<ACSProcess> processes){
        for (ACSProcess p:processes){
            p.turnaround= p.completion- p.arrival;
            p.waiting= p.turnaround- p.burst;
            p.response= p.firstStart- p.arrival;
        }
    }

    public static double averageWaitingTime(ArrayList<ACSProcess>processes){
        double total=0;
        for(ACSProcess p:processes){
            total+=p.waiting;
        }
        return total/processes.size();
    }

    public static double averageTurnaroundTime(ArrayList<ACSProcess> processes){
        double total=0;
        for(ACSProcess p:processes){
            total+=p.turnaround;
        }
        return total/processes.size();
    }
}
