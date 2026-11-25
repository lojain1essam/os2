import java.util.*;

public class ACS_Tier1_RR{
    public static int time = 0,next = 0;
    public static ACSProcess lastRun = null;
    public static ArrayList<ACSProcess> tier1 = new ArrayList<>();

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
       
       if(cs!=0){
        if (lastRun == null) {
          System.out.println("Context switch loads P" + p.id);
        }
        else if (lastRun!=p){
         System.out.println("Context switch saves P" + lastRun.id+ " and loads P" + p.id);
            time += cs;
            addProcess(processes);
        }}
        tier1.remove(0);

        int used = 0;
        while (used<quantum && p.remaining>0){
            p.remaining--;
            time++;
            used++;
            addProcess(processes);
        }

        if (p.remaining>0) tier2.add(p);
        else p.completed= true;
        lastRun= p;
    }

    public static void printfinal(ArrayList<ACSProcess> list){

        if (list.isEmpty()){
            System.out.print("[Empty]");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            ACSProcess p = list.get(i);
            System.out.print("[P" + p.id + " (remaining: " + p.remaining+ ", Priority: " + p.currentPriority + ")]");
            if (i<list.size()-1){
                System.out.print(", ");
            }
        }
    }
}

