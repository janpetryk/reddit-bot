package pl.jpetryk.redditbot.utils;

import net.dean.jraw.http.RestResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import pl.jpetryk.redditbot.model.Tweet;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jan on 17/01/15.
 */
public class ResponseCommentCreator {

    private String template;

    public ResponseCommentCreator(String template) {
        this.template = template;
    }

    public String createResponseComment(Tweet tweet) {
        template = template.replace("${posterScreenName}", tweet.getPosterScreenName());
        template = template.replace("${posterProfileUrl}", tweet.getPosterProfileUrl());
        template = template.replace("${datePosted}", convertDateToString(tweet.getDatePosted()));
        template = template.replace("${tweetUrl}", tweet.getTweetUrl());
        template = template.replace("${body}", tweet.getBody());
        return template;
    }

    private String convertDateToString(DateTime dateTime){
        return dateTime.toDateTime(DateTimeZone.UTC).toString("yyyy-MM-dd HH-mm-ss zzz");
    }


}
