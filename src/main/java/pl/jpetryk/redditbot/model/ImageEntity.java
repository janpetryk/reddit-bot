package pl.jpetryk.redditbot.model;

/**
 * Created by Jan on 22/02/15.
 */
public class ImageEntity {

    private String url;
    private String expandedUrl;
    private String rehostedUrl;

    public ImageEntity(String url, String expandedUrl) {
        this.url = url;
        this.expandedUrl = expandedUrl;
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

    public void setRehostedUrl(String rehostedUrl) {
        this.rehostedUrl = rehostedUrl;
    }
}
