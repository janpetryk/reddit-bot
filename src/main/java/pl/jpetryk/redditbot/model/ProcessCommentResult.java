package pl.jpetryk.redditbot.model;

/**
 * Created by Jan on 24/01/15.
 */
public class ProcessCommentResult {

    private boolean shouldRespond;

    private String responseMessage;

    public static final ProcessCommentResult DO_NOT_RESPOND = new ProcessCommentResult(false, null);

    private ProcessCommentResult(boolean shouldRespond, String responseMessage) {
        this.shouldRespond = shouldRespond;
        this.responseMessage = responseMessage;
    }

    public static ProcessCommentResult doNotRespond() {
        return DO_NOT_RESPOND;
    }

    public static ProcessCommentResult respondWith(String message) {
        return new ProcessCommentResult(true, message);
    }

    public boolean shouldRespond() {
        return shouldRespond;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
