package pl.jpetryk.redditbot.model;

/**
 * Created by Jan on 22/02/15.
 */
public class RehostedImageEntity {

    private String originalUrl;
    private String rehostedUrl;

    public RehostedImageEntity(String originalUrl, String rehostedUrl) {
        this.originalUrl = originalUrl;
        this.rehostedUrl = rehostedUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getRehostedUrl() {
        return rehostedUrl;
    }
}
