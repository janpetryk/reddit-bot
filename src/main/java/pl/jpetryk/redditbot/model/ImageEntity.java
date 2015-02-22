package pl.jpetryk.redditbot.model;

/**
 * Created by Jan on 22/02/15.
 */
public class ImageEntity {

    private String url;
    private String expandedUrl;
    private String rehostedUrl;
    private boolean imageRehostedSuccessfully;

    public ImageEntity(String url, String expandedUrl) {
        this.url = url;
        this.expandedUrl = expandedUrl;
        imageRehostedSuccessfully = false;
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
        if (rehostedUrl != null) {
            imageRehostedSuccessfully = true;
        }
        this.rehostedUrl = rehostedUrl;
    }

    public boolean isImageRehostedSuccessfully() {
        return imageRehostedSuccessfully;
    }
}
