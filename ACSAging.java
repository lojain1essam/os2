
import java.util.ArrayList;

public class ACSAging {

    static final int AGING_INTERVAL = 4;

   
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
}
