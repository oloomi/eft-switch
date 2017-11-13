package pos;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mohammad
 */
public class MockPOS extends Thread {

    private int POSid;
    private int numOfRequests;
    private int port;
    private int sendingInterval;
    private int numOfTimedOuts;
    private long totalResponseTime;
    private Long [] avgResTime;
    boolean test;

    public MockPOS(int POSid, int numOfRequests, int port, int sendingInterval, Long [] avgResTime, boolean test) {
        this.POSid = POSid;
        this.numOfRequests = numOfRequests;
        this.port = port;
        this.sendingInterval = sendingInterval;
        this.avgResTime = avgResTime;
        this.test = test;
        numOfTimedOuts = 0;
        totalResponseTime = 0;
    }

    @Override
    public void run() {
        System.out.println("POS" + POSid + " started...");
        for (int i = 1; i <= numOfRequests; i++) {
            try {
                Socket clientSocket = new Socket("localhost", port);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String strCardID, strPrice;
                // Sending a request
                if (test) {
                    strCardID = Long.toString(Math.round(Math.random() * 1000000000));
                    strPrice = Long.toString(Math.round(Math.random() * 500000));
                } else {
                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));                    
                    System.out.println("POS " + POSid +". Enter price: ");
                    strPrice = stdIn.readLine();
                    System.out.println("POS " + POSid +". Enter card number: ");
                    strCardID = stdIn.readLine();
                    System.out.println("POS " + POSid +". Do you have any other requests?(y/n)");
                    String cmd = stdIn.readLine();
                    if(cmd.equals("n"))
                        i = numOfRequests + 1;  // finish this iteration and get out of 'for' loop
                    else if(cmd.equals("y"))
                        ;
                    else
                        System.out.println("POS " + POSid +". Invalid choice!");
                }

                Calendar now = Calendar.getInstance();                
                String strDateTime = now.getTime().toString();
                strDateTime = strDateTime.replace(' ', '-');

                // PurchaseRequest(String trxId, String cardId, String price, String dateTime, String POSid, Socket requestSocket)
                String strRequest = "" + (i * 1000 + POSid) + "$100$" + strCardID + "$" + strPrice
                        + "$" +strDateTime + "$" + POSid +'\r';                
                outToServer.writeBytes(strRequest);
                long timeOfSend = System.currentTimeMillis();

                // Recieving its response
                String strResponse = inFromServer.readLine();
                strResponse = strResponse.replace('$', ' ');
                String[] response = strResponse.split(" ");
                if (response[0].equals("102")) {
                    System.out.print("POS " + POSid + ", Request " + (i * 1000 + POSid) + ":\n" +
                            "Status: Resolved" + " RefId: " + response[1] + " TrxId: " + response[2] + " Price: " + response[3]);
                } else if (response[0].equals("104")) {
                    System.out.print("POS " + POSid + ", Request " + (i * 1000 + POSid) + ":\n" +
                            "Status: Timed Out" + " TrxId: " + response[1]);
                    numOfTimedOuts++;
                }
                else {
                    System.out.println("Invalid response!");
                }

                // Ending the request
                strRequest = "END" + '\n';
                outToServer.writeBytes(strRequest);

                // Calculating response time
                long responseTime = System.currentTimeMillis() - timeOfSend;
                System.out.println(", Respons time = " + responseTime + " ms\n");
                totalResponseTime += responseTime;
            } catch (UnknownHostException ex) {
                Logger.getLogger(MockPOS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MockPOS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                sleep(sendingInterval);
            } catch (InterruptedException ex) {
                Logger.getLogger(MockPOS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Calculating average response time
        synchronized(avgResTime){
            avgResTime[POSid] = totalResponseTime/numOfRequests;
            avgResTime.notify();
        }
        System.out.println("\n--- POS " + POSid + ": Average Response Time = " + totalResponseTime/numOfRequests + "\n");
    }
}
