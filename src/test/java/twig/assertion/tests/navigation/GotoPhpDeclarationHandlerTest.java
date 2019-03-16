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

    private static final String NAVIGATION_TARGET_SOURCE_FILE = "TestTarget.php";
    private static final String NAVIGATION_TARGET_CHILD_SOURCE_FILE = "ChildClass.php";
    private static final String NAVIGATION_TARGET_BAM_SOURCE_FILE = "Bam.php";

    private GotoDeclarationHandlerAssertion assertion;
    private PsiElementFromFixtureFileLoader twigElementResolver;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(NAVIGATION_TARGET_SOURCE_FILE);
        myFixture.copyFileToProject(NAVIGATION_TARGET_CHILD_SOURCE_FILE);
        myFixture.copyFileToProject(NAVIGATION_TARGET_BAM_SOURCE_FILE);
        assertion = new GotoDeclarationHandlerAssertion(new GotoPhpDeclarationHandler(), myFixture.getEditor());
        twigElementResolver = new PsiElementFromFixtureFileLoader(myFixture, getTestDataPath(), TwigFileType.INSTANCE);
    }

    public String getTestDataPath() {
        return "src/test/java/twig/assertion/tests/navigation/fixtures";
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToClassDeclaration() {
        assertNavigationSuggestsFqn("\\TestTarget", "navigateToClassDeclaration.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration() {
        assertNavigationSuggestsFqn("\\TestTarget.getAnswer", "navigateToMethod.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration1() {
        assertNavigationSuggestsFqn("\\ChildClass.getText", "navigateToMethod_1.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration2() {
        assertNavigationSuggestsFqn("\\ChildClass.getTarget", "navigateToMethod_2.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration3() {
        assertNavigationSuggestsFqn("\\TestTarget.getChild", "navigateToMethod_3.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration4() {
        assertNavigationSuggestsFqn("\\TestTarget.getChild", "navigateToMethod_4.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration5() {
        assertNavigationSuggestsFqn("\\Bam.toString", "navigateToMethod_5.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToMethodDeclaration6() {
        assertNavigationSuggestsFqn("\\ChildClass.getBam", "navigateToMethod_6.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToFieldDeclaration() {
        assertNavigationSuggestsFqn("\\TestTarget.$someField", "navigateToField.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToFieldDeclaration_1() {
        assertNavigationSuggestsFqn("\\ChildClass.$childObject", "navigateToField_1.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToFieldDeclaration_2() {
        assertNavigationSuggestsFqn("\\Bam.$badabam", "navigateToField_2.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToFieldDeclaration_3() {
        assertNavigationSuggestsFqn("\\ChildClass.$childObject", "navigateToField_3.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToFieldDeclaration_4() {
        assertNavigationSuggestsFqn("\\ChildClass.$childObject", "navigateToField_4.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToFieldDeclaration_5() {
        assertNavigationSuggestsFqn("\\ChildClass.$childObject", "navigateToField_5.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToVariableDeclaration() {
        assertNavigationSuggestsAssert("myObj", "\\\\TestTarget", "navigateToVariableDeclaration.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToVariableDeclaration_1() {
        assertNavigationSuggestsAssert("myObj", "\\\\TestTarget", "navigateToVariableDeclaration_1.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NavigatesToVariableDeclaration_2() {
        assertNavigationSuggestsAssert("myChildObj", "\\\\ChildClass", "navigateToVariableDeclaration_2.twig");
    }


    @Test
    public void testGetGotoDeclarationTargets_NotNavigatesToMethodDeclaration() {
        assertNavigationSuggestsNothing("not_navigateToMethod.twig");
    }

    @Test
    public void testGetGotoDeclarationTargets_NotNavigatesToMethodDeclaration_1() {
        assertNavigationSuggestsNothing("not_navigateToMethod_1.twig");
    }


    private void assertNavigationSuggestsNothing(String fileName) {
        PsiElement navigateFromElement = twigElementResolver.resolveAtCaret(fileName);
        assertion.navigationIsEmpty(navigateFromElement);
    }

    private void assertNavigationSuggestsAssert(String expectedVariableName, String expectedTypeName, String fileName) {
        PsiElement navigateFromElement = twigElementResolver.resolveAtCaret(fileName);
        assertion.navigationContainsAssert(navigateFromElement, expectedVariableName, expectedTypeName);
    }

    private void assertNavigationSuggestsFqn(String expectedNavigationTargetFQN, String fileName) {
        PsiElement navigateFromElement = twigElementResolver.resolveAtCaret(fileName);
        assertion.navigationContainsFqn(navigateFromElement, expectedNavigationTargetFQN);
    }

    @Test
    public void testGetActionText() {
        Assert.assertNull(new GotoPhpDeclarationHandler().getActionText(getDataContextStub()));
    }

    private DataContext getDataContextStub() {
        return s -> null;
    }
}