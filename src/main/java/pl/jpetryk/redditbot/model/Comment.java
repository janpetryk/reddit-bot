package pl.jpetryk.redditbot.model;

import pl.jpetryk.redditbot.utils.CommentUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jan on 07/12/14.
 */
public class Comment {

    private String id;
    private String body;

    private Comment(Builder builder) {
        id = builder.id;
        body = builder.body;
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (!id.equals(comment.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static class Builder {
        private String id;
        private String body;

        public Builder(String id) {
            this.id = id;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }
}
