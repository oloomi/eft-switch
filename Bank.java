
import bankswitch.Switch;
import core.ICoreProxy;
import core.MockCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import pos.MockPOS;

/**
 * @author Mohammad Oloomi
 */
public class Bank {

    public static void main(String[] args) {
        // double failureRate, double responseTimeMean
        ICoreProxy coreBanking = new MockCore(0.1, 0.1);
        Switch switch1 = new Switch(coreBanking);
        switch1.run();

        /*** System Configuration ***/
        int numOfPOS = 1;
        int numOfRequests = 15;
        int sendingInterval = 100;
        /*** System Configuration ***/

        Long[] avgRespTime = new Long[numOfPOS + 1];
        for (int i = 0; i <= numOfPOS; i++) {
            avgRespTime[i] = new Long(0);
        }

        for (int i = 1; i <= numOfPOS; i++) {
            MockPOS tmpPOS = new MockPOS(i, numOfRequests, 6789, sendingInterval, avgRespTime, true);
            tmpPOS.start();
        }

        synchronized (avgRespTime) {
            for (int i = 0; i < numOfPOS; i++) {
                try {
                    avgRespTime.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (int i = 1; i <= numOfPOS; i++) {
            avgRespTime[0] += avgRespTime[i];
        }
        System.out.println("\n--------------------------------------\n");
        System.out.println("Total Average Response Time = " + avgRespTime[0] / numOfPOS + " ms");
        System.out.println("\n--------------------------------------\n");
    }
}
