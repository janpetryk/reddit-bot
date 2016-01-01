package pl.jpetryk.redditbot.model;

/**
 * Created by Jan on 06/01/15.
 */
public class PostCommentResult {

    private boolean successful;

    private String responseCommentId;

    private String errorMessage;


    private final static PostCommentResult DELETED = new PostCommentResult(false, null, "comment deleted");
    private final static PostCommentResult BANNED = new PostCommentResult(false, null, "probably banned from this sub");

    private PostCommentResult(boolean successful, String responseCommentId, String errorMessage) {
        this.successful = successful;
        this.responseCommentId = responseCommentId;
        this.errorMessage = errorMessage;
    }

    public static PostCommentResult successful(String responseCommentId) {
        return new PostCommentResult(true, responseCommentId, null);
    }

    public static PostCommentResult unsuccessful(String errorMessage) {
        return new PostCommentResult(false, null, errorMessage);
    }

    public static PostCommentResult commentDeleted() {
        return DELETED;
    }

    public static PostCommentResult bannedFromThisSub() {
        return BANNED;
    }

    public boolean isSuccess() {
        return successful;
    }

    public String getResponseCommentId() {
        return responseCommentId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
