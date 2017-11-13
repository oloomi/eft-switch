package bankswitch;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Queue;

/**
 * @author Mohammad Oloomi
 */
public class MessageSender extends Thread {

    private final Queue outgoingMsgQ;

    public MessageSender(Queue outgoingMsgQ) {
        this.outgoingMsgQ = outgoingMsgQ;
    }

    @Override
    public void run() {
        System.out.println("MessageSender started...");
        while (true) {
            synchronized (outgoingMsgQ) {
                try {
                    if (outgoingMsgQ.isEmpty()) {
                        outgoingMsgQ.wait();
                    }
                    PurchaseRequest purchaseReq = (PurchaseRequest) outgoingMsgQ.dequeue();
//                    System.out.println("MessageSender: Response '" + purchaseReq.getTrxId() + "' recieved");
                    Socket clientSocket = purchaseReq.getRequestSocket();
                    try {
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        String strResponse = purchaseReq.getResponse() + '\n';
                        outToServer.writeBytes(strResponse);
                        try {
                                SwitchRepository.getSwitchRepository().updateStatus(purchaseReq, "MessageSender");
                            } catch (SQLException se) {
                                Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, se);
                            }
//                        System.out.println("Response port for request " + purchaseReq.getTrxId() + " : " + clientSocket.getPort());
                    } catch (IOException ex) {
                        Logger.getLogger(MessageSender.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(MessageSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}
