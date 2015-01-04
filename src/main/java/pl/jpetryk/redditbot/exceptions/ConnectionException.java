package pl.jpetryk.redditbot.exceptions;

/**
 * Created by Jan on 19/12/14.
 */
public class ConnectionException extends Exception {

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }
}
