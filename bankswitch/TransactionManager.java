package bankswitch;

import core.ICoreProxy;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Queue;

/**
 * @author Mohammad Oloomi
 */
public class TransactionManager extends Thread {

    private final Queue incomingMsgQ;
    private ICoreProxy coreProxy;
    private final Queue outgoingMsgQ;

    public TransactionManager(Queue incomingMsgQ, ICoreProxy coreProxy, Queue outgoingMsgQ) {
        this.incomingMsgQ = incomingMsgQ;
        this.coreProxy = coreProxy;
        this.outgoingMsgQ = outgoingMsgQ;
    }

    @Override
    public void run() {
        System.out.println("TransactionManager started...");
        while (true) {
            synchronized (incomingMsgQ) {
                if (incomingMsgQ.isEmpty()) {
                    try {
                        incomingMsgQ.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {                    
                    Object request = null;
                    try {                        
                        request = incomingMsgQ.dequeue();                        
                        if(request.getClass().getName().equals("bankswitch.PurchaseRequest")) {
                            PurchaseHandler purchaseHandler = new PurchaseHandler((PurchaseRequest)request, coreProxy, outgoingMsgQ);
                            purchaseHandler.start();
                            try {
                                SwitchRepository.getSwitchRepository().updateStatus((PurchaseRequest) request, "TraxManager");
                            } catch (SQLException ex) {
                                Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            }
        }
    }
}
