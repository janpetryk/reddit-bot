package pl.jpetryk.redditbot.utils;

import pl.jpetryk.redditbot.model.Comment;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jan on 07/12/14.
 */
public class CommentParser {

    private static final int TWITTER_LINK_REGEX_GROUP = 3;

    private Pattern pattern;

    @Inject
    public CommentParser(@Named("twitter-status-regex") String regex) {
        this.pattern = Pattern.compile(regex);
    }


    public boolean commentMatchesRegex(Comment comment) {
        return pattern.matcher(comment.getBody()).find();
    }


    public List<String> getTwitterLinksFromComment(Comment comment) {
        List<String> twitterLinks = new ArrayList<>();
        Matcher matcher = pattern.matcher(comment.getBody());
        while (matcher.find()) {
            twitterLinks.add(matcher.group(TWITTER_LINK_REGEX_GROUP));
        }
        return twitterLinks;
    }

}
