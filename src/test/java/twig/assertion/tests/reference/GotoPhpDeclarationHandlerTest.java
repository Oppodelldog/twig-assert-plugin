package twig.assertion.tests.reference;

import com.intellij.openapi.actionSystem.DataContext;
import com.jetbrains.twig.TwigFileType;
import org.junit.Assert;
import twig.assertion.reference.GotoPhpDeclarationHandler;
import twig.assertion.tests.util.GotoHandlerAssertion;

public class GotoPhpDeclarationHandlerTest extends FixtureTest {

    private static final String NAVIGATION_TARGET_SOURCE_FILE = "TestTarget.php";

    private GotoHandlerAssertion assertion;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(NAVIGATION_TARGET_SOURCE_FILE);
        assertion = new GotoHandlerAssertion(new GotoPhpDeclarationHandler(), myFixture);
    }

    public String getTestDataPath() {
        return "src/test/java/twig/assertion/tests/reference/fixtures";
    }

    @org.testng.annotations.Test
    public void testGetGotoDeclarationTargets_NavigatesToClassDeclaration() {
        assertion.navigationContains(
                TwigFileType.INSTANCE,
                readFile("navigateToClassDeclaration.twig"),
                "\\TestTarget"
        );
    }

    @org.testng.annotations.Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration() {
        assertion.navigationContains(
                TwigFileType.INSTANCE,
                readFile("navigateToMethod.twig"),
                "\\TestTarget.getAnswer"
        );
    }

    @org.testng.annotations.Test
    public void testGetGotoDeclarationTargets_NavigatesToFieldDeclaration() {
        assertion.navigationContains(
                TwigFileType.INSTANCE,
                readFile("navigateToField.twig"),
                "\\TestTarget.$someField"
        );
    }

    @org.testng.annotations.Test
    public void testGetActionText() {
        Assert.assertNull(new GotoPhpDeclarationHandler().getActionText(getDataContextStub()));
    }

    private DataContext getDataContextStub() {
        return s -> null;
    }
}