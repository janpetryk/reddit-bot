package pl.jpetryk.redditbot.utils;

import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import pl.jpetryk.redditbot.model.ImageEntity;
import pl.jpetryk.redditbot.model.Tweet;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan on 17/01/15.
 */
public class ResponseCommentCreator {

    private String tweetResponseTemplate;
    private String footerTemplate;
    private String datePattern;
    private String twitterPicLink;
    private String imgurPicLink;

    @Inject
    public ResponseCommentCreator(@Named("response-template") String tweetResponseTemplate,
                                  @Named("footer-template") String footerTemplate,
                                  @Named("date-pattern") String datePattern,
                                  @Named("twitter-pic-link") String twitterPicLink,
                                  @Named("imgur-pic-link") String imgurPicLink) {
        this.tweetResponseTemplate = tweetResponseTemplate;
        this.footerTemplate = footerTemplate;
        this.datePattern = datePattern;
        this.twitterPicLink = twitterPicLink;
        this.imgurPicLink = imgurPicLink;

    }

    private String createResponseComment(Tweet tweet) {
        StringBuilder result = new StringBuilder(tweetResponseTemplate);
        replaceAll(result, "${posterScreenName}", tweet.getPosterScreenName());
        replaceAll(result, "${posterProfileUrl}", tweet.getPosterProfileUrl());
        replaceAll(result, "${datePosted}",
                convertDateToString(tweet.getDatePosted()));
        replaceAll(result, "${tweetUrl}", tweet.getTweetUrl());
        replaceAll(result, "${body}", prepareTweetBody(tweet));
        return result.toString();
    }

    private String prepareTweetBody(Tweet tweet) {
        StringBuilder tweetStringBuilder = new StringBuilder(tweet.getBody());
        replaceAll(tweetStringBuilder, "\n", "\n> ");
        replaceAll(tweetStringBuilder, "#", "\\#");
        replaceAll(tweetStringBuilder, "^", "\\^");

        for (ImageEntity imageEntity : tweet.getImageEntities()) {
            replaceAll(tweetStringBuilder, imageEntity.getUrl(), getImageLinks(imageEntity));
        }
        for (Map.Entry<String, String> entry : tweet.getUrlEntities().entrySet()) {
            replaceAll(tweetStringBuilder, entry.getKey(), entry.getValue());
        }
        return tweetStringBuilder.toString();
    }

    private String createRedditLink(String source, String name) {
        return "[" + name + "]" + "(" + source + ")";
    }

    private String getImageLinks(ImageEntity entity) {
        String result = createRedditLink(entity.getExpandedUrl(), twitterPicLink);
        if (entity.isImageRehostedSuccessfully()) {
            result = result + " " + createRedditLink(entity.getRehostedUrl(), imgurPicLink);
        }
        return result;
    }

    private void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }

    public String createResponseComment(List<Tweet> tweetList) {
        String result = "";
        for (Tweet tweet : tweetList) {
            result += createResponseComment(tweet);
        }
        result += footerTemplate;
        return result;
    }

    private String convertDateToString(DateTime dateTime) {
        return dateTime.toDateTime(DateTimeZone.UTC).toString(datePattern);
    }


}
