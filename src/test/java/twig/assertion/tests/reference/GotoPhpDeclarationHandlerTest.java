package twig.assertion.tests.reference;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.twig.TwigFileType;
import org.junit.Assert;
import twig.assertion.reference.GotoPhpDeclarationHandler;
import twig.assertion.tests.util.GotoDeclarationHandlerAssertion;
import twig.assertion.tests.util.PsiElementFromFixtureFileLoader;

public class GotoPhpDeclarationHandlerTest extends LightCodeInsightFixtureTestCase {

    private static final String NAVIGATION_TARGET_SOURCE_FILE = "TestTarget.php";

    private GotoDeclarationHandlerAssertion assertion;
    private PsiElementFromFixtureFileLoader twigElementResolver;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(NAVIGATION_TARGET_SOURCE_FILE);
        assertion = new GotoDeclarationHandlerAssertion(new GotoPhpDeclarationHandler(), myFixture.getEditor());
        twigElementResolver = new PsiElementFromFixtureFileLoader(myFixture, getTestDataPath(), TwigFileType.INSTANCE);
    }

    public String getTestDataPath() {
        return "src/test/java/twig/assertion/tests/reference/fixtures";
    }

    @org.testng.annotations.Test
    public void testGetGotoDeclarationTargets_NavigatesToClassDeclaration() {
        assertNavigationSuggestsFqn("\\TestTarget", "navigateToClassDeclaration.twig");
    }

    @org.testng.annotations.Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration() {
        assertNavigationSuggestsFqn("\\TestTarget.getAnswer", "navigateToMethod.twig");
    }

    @org.testng.annotations.Test
    public void testGetGotoDeclarationTargets_NavigatesToFieldDeclaration() {
        assertNavigationSuggestsFqn("\\TestTarget.$someField", "navigateToField.twig");
    }

    private void assertNavigationSuggestsFqn(String expectedNavigationTargetFQN, String fileName) {
        PsiElement navigateFromElement = twigElementResolver.resolveAtCaret(fileName);
        assertion.navigationContains(navigateFromElement, expectedNavigationTargetFQN);
    }

    @org.testng.annotations.Test
    public void testGetActionText() {
        Assert.assertNull(new GotoPhpDeclarationHandler().getActionText(getDataContextStub()));
    }

    private DataContext getDataContextStub() {
        return s -> null;
    }
}