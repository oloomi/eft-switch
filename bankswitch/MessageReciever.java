package bankswitch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import sun.misc.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mohammad Oloomi
 */
public class MessageReciever extends Thread {

    final private Queue incomingMsgQ;

    public MessageReciever(Queue incomingMsgQ) {
        this.incomingMsgQ = incomingMsgQ;
    }

    @Override
    public void run() {
        System.out.println("MessageReciever started...");
        ServerSocket welcomeSocket = null;
        try {
            welcomeSocket = new ServerSocket(6789);
            while (true) {

                Socket connectionSocket = welcomeSocket.accept();                
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                // Recieving a request message
                String strRequest = inFromClient.readLine();
//                System.out.println("MessageReciever: Request '" + strRequest + "' recieved");
                if (strRequest.equals("END")) {
                    connectionSocket.close();                    
                } else {    // Making a request object and passing it to TransactionManager via queue
                    strRequest = strRequest.replace('$',' ');
                    String[] request = strRequest.split(" ");
                    if(request[1].equals("100")) {
                        // PurchaseRequest(String trxId, String cardId, String price, String dateTime, String POSid, Socket requestSocket)
                        PurchaseRequest purchaseReq = new PurchaseRequest(request[0], request[2], request[3],
                                request[4], request[5], connectionSocket);
                        synchronized (incomingMsgQ) {
                            incomingMsgQ.enqueue(purchaseReq);
//                        System.out.println("Recieve port for request " + purchaseReq.getTrxId() + " : " + connectionSocket.getPort());
                            incomingMsgQ.notifyAll();
                            // Inserting transacion into DB
                            SwitchRepository.getSwitchRepository().insertReq(purchaseReq);                            
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageReciever.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MessageReciever.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
