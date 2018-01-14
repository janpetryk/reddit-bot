package pl.jpetryk.redditbot.utils;

public class RequireUtils {

    public static void requireNonEmpty(String valueToCheck, String message) {
        if (valueToCheck == null) {
            throw new NullPointerException(message);
        }
        if (valueToCheck.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
