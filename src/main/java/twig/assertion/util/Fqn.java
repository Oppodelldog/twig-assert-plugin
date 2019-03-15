package twig.assertion.util;

public class Fqn {
    public static String toTwigString(String s) {
        return s.replace("\\", "\\\\");
    }

    public static String fromTwigString(String s) {
        return s.replace("\\\\", "\\");
    }
}
