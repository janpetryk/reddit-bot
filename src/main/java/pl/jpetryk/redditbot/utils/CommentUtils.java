package pl.jpetryk.redditbot.utils;

import pl.jpetryk.redditbot.model.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jan on 07/12/14.
 */
public class CommentUtils {

    public static final String TWITTER_LONG_URL_PATTERN = ("https?:\\/\\/twitter\\.com\\/(?:#!\\/)?(\\w+)\\/status(es)?\\/(\\d+)");

    public static final List<String> TWITTER_PATTERN_LIST;

    static {
        TWITTER_PATTERN_LIST = Arrays.asList(TWITTER_LONG_URL_PATTERN);
    }

    private CommentUtils() {
    }

    ;

    public static boolean commentContainsTwitterLink(Comment comment) {
        for (String regex : TWITTER_PATTERN_LIST) {
            Pattern pattern = Pattern.compile(regex);
            if (pattern.matcher(comment.getBody()).find()) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getTwitterLinksFromComment(Comment comment) {
        List<String> twitterLinkList = new ArrayList<>();
        for (String regex : TWITTER_PATTERN_LIST) {
            Matcher matcher = Pattern.compile(regex).matcher(comment.getBody());
            while (matcher.find()) {
                twitterLinkList.add(matcher.group());
            }
        }
        return twitterLinkList;
    }


}
