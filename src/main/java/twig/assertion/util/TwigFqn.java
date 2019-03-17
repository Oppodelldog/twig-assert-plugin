package twig.assertion.util;

public class TwigFqn {
    public static String toTwigString(String s) {
        return s.replace("\\", "\\\\");
    }

    public static String fromTwigString(String s) {
        return s.replace("\\\\", "\\");
    }
}
