package pl.jpetryk.redditbot.model;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan on 04/01/15.
 */
public class Tweet {

    private static final String TWITTER_URL = "https://twitter.com/";

    private long id;
    private String body;
    private String poster;
    private DateTime datePosted;
    private Map<String, String> urlEntities;
    private List<ImageEntity> imageEntities;

    private Tweet(Builder builder) {
        this.id = builder.id;
        this.body = builder.body;
        this.poster = builder.poster;
        this.datePosted = builder.datePosted;
        this.urlEntities = ImmutableMap.copyOf(builder.urlEntities);
        this.imageEntities = ImmutableList.copyOf(builder.imageEntities);
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getPosterScreenName() {
        return poster;
    }

    public DateTime getDatePosted() {
        return datePosted;
    }

    public String getPosterProfileUrl() {
        return TWITTER_URL + poster + "/";
    }

    public String getTweetUrl() {
        return getPosterProfileUrl() + "status/" + id;
    }

    public List<ImageEntity> getImageEntities() {
        return imageEntities;
    }

    public Map<String, String> getUrlEntities() {
        return urlEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (id != tweet.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public static class Builder {
        private long id;
        private String body;
        private String poster;
        private DateTime datePosted;
        private Map<String, String> urlEntities;
        private List<ImageEntity> imageEntities;

        public Builder() {
            urlEntities = new HashMap<>();
            imageEntities = new LinkedList<>();
        }

        public Builder id(long id) {
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

        public Builder addUrlEntity(String key, String value) {
            urlEntities.put(key, value);
            return this;
        }

        public Builder addImageEntity(String key, String value) {
            imageEntities.add(new ImageEntity(key, value));
            return this;
        }

        public Tweet build() {
            return new Tweet(this);
        }
    }

}
