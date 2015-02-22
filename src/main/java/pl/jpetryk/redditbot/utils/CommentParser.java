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

    private Pattern pattern;

    @Inject
    public CommentParser(@Named("twitter-status-regex") String regex) {
        this.pattern = Pattern.compile(regex);
    }


    public boolean commentMatchesRegex(Comment comment) {
        return pattern.matcher(comment.getBody()).find();
    }


    public List<String> getRegexGroup(Comment comment, int groupId) {
        List<String> twitterLinks = new ArrayList<>();
        Matcher matcher = pattern.matcher(comment.getBody());
        while (matcher.find()) {
            twitterLinks.add(matcher.group(groupId));
        }
        return twitterLinks;
    }

}
