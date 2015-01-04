package pl.jpetryk.redditbot.exceptions;

/**
 * Created by Jan on 19/12/14.
 */
public class InvalidCredentialsException extends Exception {

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

}
