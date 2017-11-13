package core;

/**
 * @author Mohammad Oloomi
 */
public interface ICoreProxy {

    public boolean purchaseRequest() throws CoreTimeoutException;
    public int getRefId();
}
