package twig.assertion.tests.reference;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;

public abstract class FixtureTest extends LightCodeInsightFixtureTestCase {

    String readFile(String filePath) {
        String fullFilePath = Paths.get(getTestDataPath(), filePath).toString();
        try {
            StringBuilder sb = new StringBuilder();
            Scanner scan = new Scanner(new File(fullFilePath));
            while (scan.hasNext()) {
                sb.append(scan.nextLine()).append('\n');
            }
            return sb.toString();
        } catch (Throwable t) {
            fail(String.format("failed to read file from %s:  %s", fullFilePath, t.getMessage()));
        }
        return "";
    }
}
