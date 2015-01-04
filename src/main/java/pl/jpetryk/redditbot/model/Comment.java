package pl.jpetryk.redditbot.model;

/**
 * Created by Jan on 07/12/14.
 */
public class Comment {

    private String commentId;
    private String body;
    private String linkId;

    private Comment(Builder builder) {
        commentId = builder.id;
        body = builder.body;
        linkId = builder.linkId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getBody() {
        return body;
    }

    public String getLinkId() {
        return linkId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (commentId != null ? !commentId.equals(comment.commentId) : comment.commentId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return commentId != null ? commentId.hashCode() : 0;
    }

    public static class Builder {
        private String id;
        private String body;
        private String linkId;

        public Builder commentId(String id) {
            this.id = id;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder linkId(String linkId) {
            this.linkId = linkId;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }
}
