package twig.assertion.tests.refactoring;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.junit.Assert;
import org.testng.annotations.Test;
import twig.assertion.navigation.GotoPhpDeclarationHandler;

public class RefactoringProviderTest extends LightCodeInsightFixtureTestCase {

    private static final String FILE_CLASS_TO_RENAME = "Foo.php";
    private static final String FILE_TEMPLATE = "template.twig";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(FILE_CLASS_TO_RENAME);
        myFixture.copyFileToProject(FILE_TEMPLATE);
    }

    public String getTestDataPath() {
        return "src/test/java/twig/assertion/tests/refactoring/fixtures";
    }

    @Test
    public void testRefactorPhpClass() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_CLASS_TO_RENAME, FILE_TEMPLATE);
        PhpClass phpClass = myFixture.findElementByText("Foo", PhpClass.class);
        PsiElement twigAssertElement = psiFiles[1].getFirstChild();

        String expectedBeforeRenaming = "{% assert myVar1 \"\\\\Foo\" %}";
        String expectedAfterRenaming = "{% assert myVar1 \"\\\\Bar\" %}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, twigAssertElement.getText());

        myFixture.renameElement(phpClass, "Bar");

        assertEquals(expectedAfterRenaming, twigAssertElement.getText());

    }

    private void failIfNotEquals(@SuppressWarnings("SameParameterValue") String message, String expected, String actual) {
        if (!expected.equals(actual)) {
            failNotEquals(message, expected, actual);
        }
    }

    @Test
    public void testGetActionText() {
        Assert.assertNull(new GotoPhpDeclarationHandler().getActionText(getDataContextStub()));
    }

    private DataContext getDataContextStub() {
        return s -> null;
    }
}