package pl.jpetryk.redditbot.parser;

import pl.jpetryk.redditbot.model.Comment;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jan on 07/12/14.
 */
public class BaseCommentParser implements CommentParser {

    private static final int TWITTER_LINK_REGEX_GROUP = 3;

    private Pattern pattern;

    @Inject
    public BaseCommentParser(@Named("twitter-status-regex") String regex) {
        this.pattern = Pattern.compile(regex);
    }


    public boolean commentMatchesRegex(Comment comment) {
        return pattern.matcher(comment.getBody()).find();
    }


    @Override
    public Collection<String> getTwitterStatusIdsFromComment(Comment comment) {
        Set<String> twitterLinks = new LinkedHashSet<>();
        Matcher matcher = pattern.matcher(comment.getBody());
        while (matcher.find()) {
            twitterLinks.add(matcher.group(TWITTER_LINK_REGEX_GROUP));
        }
        return twitterLinks;
    }

}
