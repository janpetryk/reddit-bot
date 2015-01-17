package pl.jpetryk.redditbot.utils;

import pl.jpetryk.redditbot.model.Comment;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jan on 07/12/14.
 */
public class CommentParser {

    private Pattern pattern;

    public CommentParser(String regex) {
        this.pattern = Pattern.compile(regex);
    }


    public boolean commentMatchesRegex(Comment comment) {
        return pattern.matcher(comment.getBody()).find();
    }


    public Set<String> getRegexGroup(Comment comment, int groupId) {
        Set<String> twitterLinks = new HashSet<>();
        Matcher matcher = pattern.matcher(comment.getBody());
        while (matcher.find()) {
            twitterLinks.add(matcher.group(groupId));
        }
        return twitterLinks;
    }

}
