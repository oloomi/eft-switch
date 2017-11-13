package bankswitch;

import java.net.Socket;

/**
 * @author Mohammad Oloomi
 */
public class PurchaseRequest {

    private String trxId;
    private String cardId;
    private String price;
    private String dateTime;    
    private String POSid;
    private String response;
    private String refId;
    private Socket requestSocket;

    public PurchaseRequest(String trxId, String cardId, String price, String dateTime, String POSid, Socket requestSocket) {
        this.trxId = trxId;
        this.cardId = cardId;
        this.price = price;
        this.dateTime = dateTime;        
        this.POSid = POSid;        
        this.requestSocket = requestSocket;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTrxId() {
        return trxId;
    }

    public Socket getRequestSocket() {
        return requestSocket;
    }

    public String getResponse() {
        return response;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRefId() {
        return refId;
    }

    public String getPrice() {
        return price;
    }

    public String getPOSid() {
        return POSid;
    }

    public String getCardId() {
        return cardId;
    }

    public String getDateTime() {
        return dateTime;
    }
    
}
