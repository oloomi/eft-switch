package core;

/**
 * @author Mohammad
 */
public class CoreTimeoutException extends Exception {

    /**
     * Creates a new instance of <code>CoreTimeoutException</code> without detail message.
     */
    public CoreTimeoutException() {
    }


    /**
     * Constructs an instance of <code>CoreTimeoutException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CoreTimeoutException(String msg) {
        super(msg);
    }
}
