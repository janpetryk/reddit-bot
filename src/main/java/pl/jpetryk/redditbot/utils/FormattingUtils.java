package pl.jpetryk.redditbot.utils;

/**
 * Created by Jan on 2016-03-08.
 */
public class FormattingUtils {

    public static final String NEW_LINE = "\n\n>";

    private FormattingUtils(){}

    public static String getNoParticipationRedditLink(String url) {
        return url.replace("reddit.com", "np.reddit.com");
    }

    public static String createRedditHyperLink(String source, String name) {
        return "[" + name + "]" + "(" + source + ")";
    }

    public static String escapeRedditSpecialCharacters(String string) {
        StringBuilder tweetStringBuilder = new StringBuilder(string);
        replaceAll(tweetStringBuilder, ">", "\\>");
        replaceAll(tweetStringBuilder, "\n", "\n\n> ");
        replaceAll(tweetStringBuilder, "#", "\\#");
        replaceAll(tweetStringBuilder, "^", "\\^");
        replaceAll(tweetStringBuilder, "*", "\\*");
        replaceAll(tweetStringBuilder, "_", "\\_");
        return tweetStringBuilder.toString();
    }


    private static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }
}
