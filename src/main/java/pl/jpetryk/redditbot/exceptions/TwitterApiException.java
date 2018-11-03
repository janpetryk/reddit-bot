package pl.jpetryk.redditbot.exceptions;

/**
 * Created by Jan on 09/01/15.
 */
public class TwitterApiException extends RuntimeException {

	public TwitterApiException(Throwable cause, boolean rateLimitExceeded, long miliSecondsUntilReset, int errorCode) {
		super(cause);
		this.miliSecondsUntilReset = miliSecondsUntilReset;
		this.rateLimitExceeded = rateLimitExceeded;
		this.errorCode = errorCode;
	}

	private boolean rateLimitExceeded;

	private long miliSecondsUntilReset;

	private int errorCode;

	public boolean isRateLimitExceeded() {
		return rateLimitExceeded;
	}

	public long getMiliSecondsUntilReset() {
		return miliSecondsUntilReset;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
