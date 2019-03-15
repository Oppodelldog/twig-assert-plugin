package twig.assertion.tests.completion;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.twig.TwigFileType;
import org.testng.annotations.Test;
import twig.assertion.tests.util.FixtureFileReader;

import java.nio.file.Paths;
import java.util.List;

public class CompletionProvidersTest extends LightCodeInsightFixtureTestCase {

    private static final String NAVIGATION_TARGET_SOURCE_FILE = "CompletionTestTarget.php";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(NAVIGATION_TARGET_SOURCE_FILE);
    }

    public String getTestDataPath() {
        return "src/test/java/twig/assertion/tests/completion/fixtures";
    }

    @Test
    public void testCompleteMember() {
        completionContainsAssert(new String[]{"someField", "getValue"}, "completeMember.twig");
    }

    @Test
    public void testCompleteFQCN() {
        completionContainsAssert(new String[]{"CompletionTestTarget"}, "completeFQCN.twig");
    }

    @Test
    public void testCompleteAssertTag() {
        completionContainsAssert(new String[]{"assert"}, "completeAssert.twig");
    }

    private void completionContainsAssert(String[] expectedLookupString, String templateFileName) {
        String fullFilePath = Paths.get(getTestDataPath(), templateFileName).toString();
        myFixture.configureByText(TwigFileType.INSTANCE, FixtureFileReader.readFile(fullFilePath));
        myFixture.completeBasic();

        assertInLookupList(expectedLookupString);
    }

    private void assertInLookupList(String[] expectedSuggestions) {
        boolean found = false;
        List<String> suggestions = myFixture.getLookupElementStrings();
        if (suggestions == null) {
            fail("expected lookup list not be empty, but it was");
        }
        for (String suggestion : suggestions) {
            for (String expectedSuggestion : expectedSuggestions) {
                if (suggestion.contains(expectedSuggestion)) {
                    found = true;
                }
            }
        }

        if (!found) {
            fail(String.format("expected to find %s in %s, but did not", String.join(", ", expectedSuggestions), String.join(",", suggestions)));
        }
    }
}
