package pl.jpetryk.redditbot.model;


import org.joda.time.DateTime;

/**
 * Created by Jan on 04/01/15.
 */
public class Tweet {

    private String id;
    private String body;
    private String poster;
    private DateTime datePosted;

    private Tweet(Builder builder) {
        this.id = builder.id;
        this.body = builder.body;
        this.poster = builder.poster;
        this.datePosted = builder.datePosted;
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getPoster() {
        return poster;
    }

    public DateTime getDatePosted() {
        return datePosted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (id != null ? !id.equals(tweet.id) : tweet.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static class Builder {
        private String id;
        private String body;
        private String poster;
        private DateTime datePosted;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder poster(String poster) {
            this.poster = poster;
            return this;
        }

        public Builder datePosted(DateTime datePosted) {
            this.datePosted = datePosted;
            return this;
        }

        public Tweet build() {
            return new Tweet(this);
        }
    }

}
