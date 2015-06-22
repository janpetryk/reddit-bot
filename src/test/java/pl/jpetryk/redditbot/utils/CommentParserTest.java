package pl.jpetryk.redditbot.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.jpetryk.redditbot.model.Comment;

/**
 * Created by Jan on 07/12/14.
 */
public class CommentParserTest {

    private static final String STATUS_1_ID = "534011965132120064";
    private static final String LONG_STATUS_URL_1 = "https://twitter.com/peterritchie/status/" + STATUS_1_ID;
    private static final String STATUS_2_ID = "494683729735196673";
    private static final String LONG_STATUS_URL_2 = "https://twitter.com/unidanbiology/status/" + STATUS_2_ID;
    private CommentParser parser;
    private PropertiesReader properties = new PropertiesReader("resources/twitter.properties");

    @Before
    public void init() {
        parser = new CommentParser(properties.getProperty("twitter-status-regex"));
    }


    @Test
    public void testCommentContainsLongTwitterLinkInTheMiddle() {
        String body = "asdadsaddasddsad" + LONG_STATUS_URL_1 + " asdasdasdasd";
        assertCommentContainsTwitterLink(body);
        assertSelectedGroupIsEqualToGiven(body, STATUS_1_ID);
    }

    @Test
    public void testCommentContainsLongTwitterLinkInTheBeginning() {
        String body = LONG_STATUS_URL_1 + "asdasdsa";
        assertCommentContainsTwitterLink(body);
        assertSelectedGroupIsEqualToGiven(body, STATUS_1_ID);

    }

    @Test
    public void testCommentContainsLongTwitterLinkInTheEnd() {
        String body = "asdadadsadsasd" + LONG_STATUS_URL_1;
        assertCommentContainsTwitterLink(body);
        assertSelectedGroupIsEqualToGiven(body, STATUS_1_ID);

    }

    @Test
    public void testCommentContainsOnlyLongTwitterLink() {
        String body = LONG_STATUS_URL_1;
        assertCommentContainsTwitterLink(body);
        assertSelectedGroupIsEqualToGiven(body, STATUS_1_ID);
    }

    @Test
    public void testCommentDoesNotContainTwitterLink() {
        String body = "asdadsasdads";
        Comment comment = prepareComment(body);
        Assert.assertFalse(parser.commentMatchesRegex(comment));
        Assert.assertEquals(0, parser.getTwitterLinksFromComment(comment).size());
    }

    @Test
    public void testMultipleLinksInOneComment() {
        String body = "asdasdasdasd" + LONG_STATUS_URL_1 + "asdadsasdasd" + LONG_STATUS_URL_2;
        assertCommentContainsTwitterLink(body);
        assertSelectedGroupIsEqualToGiven(body, STATUS_1_ID);
        assertSelectedGroupIsEqualToGiven(body, STATUS_2_ID);
    }


    private void assertCommentContainsTwitterLink(String body) {
        Comment comment = prepareComment(body);
        Assert.assertTrue(parser.commentMatchesRegex(comment));
    }

    private void assertSelectedGroupIsEqualToGiven(String body, String twitterLink) {
        for (String string : parser.getTwitterLinksFromComment(prepareComment(body))) {
            if (twitterLink.equals(string)) {
                return;
            }
        }
        Assert.fail();
    }


    private Comment prepareComment(String body) {
        return new Comment.Builder().commentId("asdasda").body(body).build();
    }
}
