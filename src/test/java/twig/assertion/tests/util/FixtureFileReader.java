package twig.assertion.tests.util;

import junit.framework.TestCase;

import java.io.File;
import java.util.Scanner;

public class FixtureFileReader {

    public static String readFile(String fullFilePath) {
        try {
            StringBuilder sb = new StringBuilder();
            Scanner scan = new Scanner(new File(fullFilePath));
            while (scan.hasNext()) {
                sb.append(scan.nextLine()).append('\n');
            }
            return sb.toString();
        } catch (Throwable t) {
            TestCase.fail(String.format("failed to read file from %s:  %s", fullFilePath, t.getMessage()));
        }
        return "";
    }
}
