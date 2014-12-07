package pl.jpetryk.redditbot.utils;

import org.junit.Assert;
import org.junit.Test;
import pl.jpetryk.redditbot.model.Comment;

/**
 * Created by Jan on 07/12/14.
 */
public class CommentUtilsTest {

    private static final String LONG_STATUS_URL_1 = "https://twitter.com/peterritchie/status/534011965132120064";
    private static final String LONG_STATUS_URL_2 = "https://twitter.com/unidanbiology/status/494683729735196673";

    @Test
    public void testCommentContainsLongTwitterLinkInTheMiddle() {
        String body = "asdadsaddasddsad" + LONG_STATUS_URL_1 + " asdasdasdasd";
        assertCommentContainsTwitterLink(body);
        assertSelectedLinkIsEqualToGiven(body, LONG_STATUS_URL_1);
    }

    @Test
    public void testCommentContainsLongTwitterLinkInTheBeginning() {
        String body = LONG_STATUS_URL_1 + "asdasdsa";
        assertCommentContainsTwitterLink(body);
        assertSelectedLinkIsEqualToGiven(body, LONG_STATUS_URL_1);

    }

    @Test
    public void testCommentContainsLongTwitterLinkInTheEnd() {
        String body = "asdadadsadsasd" + LONG_STATUS_URL_1;
        assertCommentContainsTwitterLink(body);
        assertSelectedLinkIsEqualToGiven(body, LONG_STATUS_URL_1);

    }

    @Test
    public void testCommentContainsOnlyLongTwitterLink() {
        String body = LONG_STATUS_URL_1;
        assertCommentContainsTwitterLink(body);
        assertSelectedLinkIsEqualToGiven(body, LONG_STATUS_URL_1);
    }

    @Test
    public void testCommentDoesNotContainTwitterLink() {
        String body = "asdadsasdads";
        Comment comment = prepareComment(body);
        Assert.assertFalse(CommentUtils.commentContainsTwitterLink(comment));
        Assert.assertEquals(0, CommentUtils.getTwitterLinksFromComment(comment).size());
    }

    @Test
    public void testMultipleLinksInOneComment() {
        String body = "asdasdasdasd" + LONG_STATUS_URL_1 + "asdadsasdasd" + LONG_STATUS_URL_2;
        assertCommentContainsTwitterLink(body);
        assertSelectedLinkIsEqualToGiven(body,LONG_STATUS_URL_1);
        assertSelectedLinkIsEqualToGiven(body,LONG_STATUS_URL_2);
    }


    private void assertCommentContainsTwitterLink(String body) {
        Comment comment = prepareComment(body);
        Assert.assertTrue(CommentUtils.commentContainsTwitterLink(comment));
    }

    private void assertSelectedLinkIsEqualToGiven(String body, String twitterLink) {
        for (String string : CommentUtils.getTwitterLinksFromComment(prepareComment(body))) {
            if (twitterLink.equals(string)) {
                return;
            }
        }
        Assert.fail();
    }


    private Comment prepareComment(String body) {
        return Comment.builder().id("asdasda").body(body).build();
    }
}
