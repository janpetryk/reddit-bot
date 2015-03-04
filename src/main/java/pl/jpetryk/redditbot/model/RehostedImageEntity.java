package pl.jpetryk.redditbot.model;

/**
 * Created by Jan on 22/02/15.
 */
public class RehostedImageEntity {

    private String url;
    private String expandedUrl;
    private String rehostedUrl;

    public RehostedImageEntity(String url, String expandedUrl, String rehostedUrl) {
        this.url = url;
        this.expandedUrl = expandedUrl;
        this.rehostedUrl = rehostedUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getExpandedUrl() {
        return expandedUrl;
    }

    public String getRehostedUrl() {
        return rehostedUrl;
    }
}
