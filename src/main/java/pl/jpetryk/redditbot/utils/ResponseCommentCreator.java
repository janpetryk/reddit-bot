package pl.jpetryk.redditbot.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import pl.jpetryk.redditbot.model.Tweet;

import java.util.List;

/**
 * Created by Jan on 17/01/15.
 */
public class ResponseCommentCreator {

    private String tweetResponseTemplate;
    private String footerTemplate;

    public ResponseCommentCreator(String tweetResponseTemplate, String footerTemplate) {
        this.tweetResponseTemplate = tweetResponseTemplate;
        this.footerTemplate = footerTemplate;
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
        for (String string : tweet.getMediaEntities().keySet()) {
            replaceAll(tweetStringBuilder, string, tweet.getMediaEntities().get(string));
        }
        for (String string : tweet.getUrlEntities().keySet()) {
            replaceAll(tweetStringBuilder, string, tweet.getUrlEntities().get(string));
        }
        return tweetStringBuilder.toString();
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
        return dateTime.toDateTime(DateTimeZone.UTC).toString("yyyy-MM-dd HH:mm:ss zzz");
    }


}
