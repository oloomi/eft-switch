package bankswitch;

import core.ICoreProxy;
import sun.misc.Queue;

/**
 * @author Mohammad Oloomi
 */
public abstract class TransactionHandler extends Thread {
   
    protected ICoreProxy coreProxy;
    protected final Queue outgoingMsgQ;

    TransactionHandler(ICoreProxy coreProxy, Queue outgoingMsgQ) {        
        this.coreProxy = coreProxy;
        this.outgoingMsgQ = outgoingMsgQ;
    }
}
