package pl.jpetryk.redditbot.model;

import org.joda.time.DateTime;
import pl.jpetryk.redditbot.utils.RedditPrefixes;

/**
 * Created by Jan on 07/12/14.
 */
public class Comment {

    private static final String REDDIT_URL = "http://reddit.com/";


    private String commentId;
    private String body;
    private String linkId;
    private String author;
    private DateTime created;
    private String linkUrl;
    private String subreddit;

    private Comment(Builder builder) {
        commentId = builder.commentId;
        body = builder.body;
        linkId = builder.linkId;
        author = builder.author;
        created = builder.created;
        linkUrl = builder.linkUrl;
        subreddit = builder.subreddit;
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

    public String getAuthor() {
        return author;
    }

    public DateTime getCreated() {
        return created;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getCommentFullName() {
        return RedditPrefixes.COMMENT_PREFIX + commentId;
    }

    public String getLinkFullName() {
        return RedditPrefixes.LINK_PREFIX + linkId;
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

    @Override
    public String toString() {
        return body;
    }

    public static class Builder {
        private String commentId;
        private String body;
        private String linkId;
        private String author;
        private DateTime created;
        private String linkUrl;
        private String subreddit;

        public Builder commentId(String id) {
            this.commentId = id;
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

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder created(DateTime datePosted) {
            this.created = datePosted;
            return this;
        }

        public Builder linkUrl(String url) {
            this.linkUrl = url;
            return this;
        }

        public Builder subreddit(String subreddit) {
            this.subreddit = subreddit;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }
}
