package pl.jpetryk.redditbot.model;

/**
 * Created by Jan on 07/12/14.
 */
public class Comment {

    private String id;
    private String body;

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public static Builder builder() {
        return new Builder();
    }

    private Comment(Builder builder) {
        id = builder.id;
        body = builder.body;
    }

    public static class Builder {
        private String id;
        private String body;

        public Builder id(String id) {
            this.id = id;
            return this;
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
