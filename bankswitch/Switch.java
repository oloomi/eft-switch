package bankswitch;

import core.ICoreProxy;
import sun.misc.Queue;

/**
 * @author Mohammad Oloomi
 */
public class Switch{

    private Queue incomingMsgQ;
    private Queue outgoingMsgQ;
    private ICoreProxy coreProxy;

    public Switch(ICoreProxy coreProxy) {
        incomingMsgQ = new Queue();
        outgoingMsgQ = new Queue();
        this.coreProxy = coreProxy;
    }
    
    public void run() {
        MessageReciever messageReciver = new MessageReciever(incomingMsgQ);
        messageReciver.start();
        TransactionManager trxManager = new TransactionManager(incomingMsgQ, coreProxy, outgoingMsgQ);
        trxManager.start();
        MessageSender messageSender = new MessageSender(outgoingMsgQ);
        messageSender.start();

    }
}
