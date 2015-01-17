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
        String result = tweetResponseTemplate;
        result = result.replace("${posterScreenName}", tweet.getPosterScreenName());
        result = result.replace("${posterProfileUrl}", tweet.getPosterProfileUrl());
        result = result.replace("${datePosted}",
                convertDateToString(tweet.getDatePosted()));
        result = result.replace("${tweetUrl}", tweet.getTweetUrl());
        result = result.replace("${body}", tweet.getBody());
        return result;
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
