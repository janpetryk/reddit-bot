package pl.jpetryk.redditbot.exceptions;

/**
 * Created by Jan on 04/01/15.
 */
public class NetworkConnectionException extends Exception {


    public NetworkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkConnectionException(String message) {
        super(message);
    }

    public NetworkConnectionException(Throwable cause) {
        super(cause);
    }

}
