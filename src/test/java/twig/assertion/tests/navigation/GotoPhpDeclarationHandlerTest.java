package twig.assertion.tests.navigation;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.twig.TwigFileType;
import org.junit.Assert;
import org.testng.annotations.Test;
import twig.assertion.navigation.GotoPhpDeclarationHandler;
import twig.assertion.tests.util.GotoDeclarationHandlerAssertion;
import twig.assertion.tests.util.PsiElementFromFixtureFileLoader;

public class GotoPhpDeclarationHandlerTest extends LightCodeInsightFixtureTestCase {

    private static final String NAVIGATION_TARGET_SOURCE_FILE = "CompletionTestTargetTestTarget.php";

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
        return "src/test/java/twig/assertion/tests/navigation/fixtures";
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToClassDeclaration() {
        assertNavigationSuggestsFqn("\\CompletionTestTarget", "navigateToClassDeclaration.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration() {
        assertNavigationSuggestsFqn("\\CompletionTestTarget.getAnswer", "navigateToMethod.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToFieldDeclaration() {
        assertNavigationSuggestsFqn("\\CompletionTestTarget.$someField", "navigateToField.twig");
    }

    private void assertNavigationSuggestsFqn(String expectedNavigationTargetFQN, String fileName) {
        PsiElement navigateFromElement = twigElementResolver.resolveAtCaret(fileName);
        assertion.navigationContains(navigateFromElement, expectedNavigationTargetFQN);
    }

    @Test
    public void testGetActionText() {
        Assert.assertNull(new GotoPhpDeclarationHandler().getActionText(getDataContextStub()));
    }

    private DataContext getDataContextStub() {
        return s -> null;
    }
}