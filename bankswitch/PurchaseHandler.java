package bankswitch;

import core.CoreTimeoutException;
import core.ICoreProxy;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Queue;

/**
 * @author Mohammad Oloomi
 */
public class PurchaseHandler extends TransactionHandler {

    private PurchaseRequest purchaseReq = null;

    public PurchaseHandler(PurchaseRequest purchaseReq, ICoreProxy coreProxy, Queue outgoingMsgQ) {
        super(coreProxy, outgoingMsgQ);
        this.purchaseReq = purchaseReq;
    }

    @Override
    public void run() {
//        System.out.println("PurchaseHandler started...");        
        try {
            if (coreProxy.purchaseRequest()) {
                
                synchronized (outgoingMsgQ) {
                    purchaseReq.setRefId(Integer.toString(coreProxy.getRefId()));
                    //String strResponse = "PurchaseRes" + purchaseReq.getTrxId();
                    String strResponse = "102$" + purchaseReq.getRefId() + "$" + 
                            purchaseReq.getTrxId() + "$" + purchaseReq.getPrice();
                    purchaseReq.setResponse(strResponse);
                    outgoingMsgQ.enqueue(purchaseReq);
                    try {
                        SwitchRepository.getSwitchRepository().updateRefId(purchaseReq, purchaseReq.getRefId());
                        SwitchRepository.getSwitchRepository().updateResponse(purchaseReq, purchaseReq.getResponse());
                        SwitchRepository.getSwitchRepository().updateStatus(purchaseReq, "PurchaseHandlerRes");
                    } catch (SQLException ex) {
                        Logger.getLogger(PurchaseHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    System.out.println("PurchaseHandler: Request '" + purchaseReq.getTrxId() + "' responded");
                    outgoingMsgQ.notifyAll();
                }
            }
        } catch (CoreTimeoutException ex) {
            synchronized (outgoingMsgQ) {
                //String strResponse = "PurchaseTO " + purchaseReq.getTrxId();
                String strResponse = "104$" + purchaseReq.getTrxId();
                purchaseReq.setResponse(strResponse);
                outgoingMsgQ.enqueue(purchaseReq);
                try {
                        SwitchRepository.getSwitchRepository().updateResponse(purchaseReq, purchaseReq.getResponse());
                        SwitchRepository.getSwitchRepository().updateStatus(purchaseReq, "PurchaseHandlerTO");
                    } catch (SQLException se) {
                        Logger.getLogger(PurchaseHandler.class.getName()).log(Level.SEVERE, null, se);
                    }
//                System.out.println("PurchaseHandler: Request '" + purchaseReq.getTrxId() + "' timed out");
                outgoingMsgQ.notifyAll();
            }            
        }
    }
}
