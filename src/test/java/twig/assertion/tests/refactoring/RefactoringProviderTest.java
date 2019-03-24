package twig.assertion.tests.refactoring;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.testng.annotations.Test;
import twig.assertion.navigation.GotoPhpDeclarationHandler;

public class RefactoringProviderTest extends LightCodeInsightFixtureTestCase {

    private static final String FILE_PHP_FOO_CLASS = "Foo.php";
    private static final String FILE_PHP_BAZ_CLASS = "Baz.php";
    private static final String FILE_TEMPLATE_RENAME_CLASS = "renameClass.twig";
    private static final String FILE_TEMPLATE_NOT_RENAME_CLASS = "not_renameClass.twig";
    private static final String FILE_TEMPLATE_RENAME_FIELD = "renameField.twig";
    private static final String FILE_TEMPLATE_RENAME_FIELD_1 = "renameField_1.twig";
    private static final String FILE_TEMPLATE_RENAME_FIELD_2 = "renameField_2.twig";
    private static final String FILE_TEMPLATE_RENAME_FIELD_3 = "renameField_3.twig";
    private static final String FILE_TEMPLATE_RENAME_FIELD_4 = "renameField_4.twig";
    private static final String FILE_TEMPLATE_RENAME_FIELD_5 = "renameField_5.twig";
    private static final String FILE_TEMPLATE_RENAME_FIELD_6 = "renameField_6.twig";

    private static final String FILE_TEMPLATE_NOT_RENAME_FIELD = "not_renameField.twig";
    private static final String FILE_TEMPLATE_NOT_RENAME_FIELD_1 = "not_renameField_1.twig";
    private static final String FILE_TEMPLATE_NOT_RENAME_FIELD_2 = "not_renameField_2.twig";
    private static final String FILE_TEMPLATE_NOT_RENAME_FIELD_3 = "not_renameField_3.twig";
    private static final String FILE_TEMPLATE_NOT_RENAME_FIELD_4 = "not_renameField_4.twig";
    private static final String FILE_TEMPLATE_NOT_RENAME_FIELD_5 = "not_renameField_5.twig";

    private static final String FILE_TEMPLATE_RENAME_METHOD = "renameMethod.twig";
    private static final String FILE_TEMPLATE_RENAME_METHOD_1 = "renameMethod_1.twig";
    private static final String FILE_TEMPLATE_RENAME_METHOD_2 = "renameMethod_2.twig";
    private static final String FILE_TEMPLATE_RENAME_METHOD_3 = "renameMethod_3.twig";
    private static final String FILE_TEMPLATE_RENAME_METHOD_4 = "renameMethod_4.twig";
    private static final String FILE_TEMPLATE_RENAME_METHOD_5 = "renameMethod_5.twig";
    private static final String FILE_TEMPLATE_RENAME_METHOD_6 = "renameMethod_6.twig";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(FILE_PHP_FOO_CLASS);
        myFixture.copyFileToProject(FILE_PHP_BAZ_CLASS);
        myFixture.copyFileToProject(FILE_TEMPLATE_RENAME_CLASS);
    }

    public String getTestDataPath() {
        return "src/test/java/twig/assertion/tests/refactoring/fixtures";
    }

    @Test
    public void testRefactorPhpClass_renameClass() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_TEMPLATE_RENAME_CLASS);
        PhpClass phpClass = myFixture.findElementByText("Foo", PhpClass.class);
        PsiElement twigAssertElement = psiFiles[1].getFirstChild();

        String expectedBeforeRenaming = "{% assert myVar1 \"\\\\Foo\" %}";
        String expectedAfterRenaming = "{% assert myVar1 \"\\\\Bar\" %}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, twigAssertElement.getText());

        myFixture.renameElement(phpClass, "Bar");

        assertEquals(expectedAfterRenaming, twigAssertElement.getText());
    }

    @Test
    public void testRefactorPhpClass_notRenameClass() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_TEMPLATE_NOT_RENAME_CLASS);
        PhpClass phpClass = myFixture.findElementByText("Foo", PhpClass.class);
        PsiElement twigAssertElement = psiFiles[1].getFirstChild();

        String expectedBeforeRenaming = "{% set myVar1 = \"\\\\Foo\" %}";
        String expectedAfterRenaming = "{% set myVar1 = \"\\\\Foo\" %}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, twigAssertElement.getText());

        myFixture.renameElement(phpClass, "Bar");

        assertEquals(expectedAfterRenaming, twigAssertElement.getText());
    }

    @Test
    public void testRefactorPhpClassField_renameTwigField() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_FIELD);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1.fooField.fooField.fooField }}";
        String expectedAfterRenaming = "{{ myVar1.barField.fooField.barField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_renameTwigField_1() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_FIELD_1);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{% if myVar1.fooField.fooField.fooField %}{% endif %}";
        String expectedAfterRenaming = "{% if myVar1.barField.fooField.barField %}{% endif %}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_renameTwigField_2() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_FIELD_2);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1.fooField.getSibling().fooField }}";
        String expectedAfterRenaming = "{{ myVar1.barField.getSibling().barField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_renameTwigField_3() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_FIELD_3);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1.fooField.getSibling().fooField.fooField.getSibling().getSibling().fooField }}";
        String expectedAfterRenaming = "{{ myVar1.barField.getSibling().barField.fooField.getSibling().getSibling().barField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_renameTwigField_4() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_FIELD_4);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1.fooField.fooField.fooField }}";
        String expectedAfterRenaming = "{{ myVar1.barField.fooField.barField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_renameTwigField_5() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_FIELD_5);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1.fooField.barField.fooField }}";
        String expectedAfterRenaming = "{{ myVar1.barField.barField.barField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_renameTwigField_6() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_FIELD_6);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myBaz.fooField.fooField.fooField }}";
        String expectedAfterRenaming = "{{ myBaz.fooField.barField.fooField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_notRenameTwigField() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_NOT_RENAME_FIELD);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1 fooField }}";
        String expectedAfterRenaming = "{{ myVar1 fooField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_notRenameTwigField_1() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_NOT_RENAME_FIELD_1);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{% if myVar1.fooField.fooField == fooField %}";
        String expectedAfterRenaming = "{% if myVar1.barField.fooField == fooField %}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_notRenameTwigField_2() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_NOT_RENAME_FIELD_2);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1.fooField.getSibling()fooField }}";
        String expectedAfterRenaming = "{{ myVar1.barField.getSibling()fooField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_notRenameTwigField_3() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_NOT_RENAME_FIELD_3);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1.fooField.getSibling()..getSibling().getSibling().fooField }}";
        String expectedAfterRenaming = "{{ myVar1.barField.getSibling()..getSibling().getSibling().fooField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_notRenameTwigField_4() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_NOT_RENAME_FIELD_4);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1.fooField.fooField..fooField }}";
        String expectedAfterRenaming = "{{ myVar1.barField.fooField..fooField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassField_notRenameTwigField_5() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_NOT_RENAME_FIELD_5);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();

        String expectedBeforeRenaming = "{{ myVar1 .fooField }}";
        String expectedAfterRenaming = "{{ myVar1 .fooField }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElementAtCaret("barField");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassMethod_renameTwigMethodCall() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_METHOD);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();
        PsiElement methodToRename = getMethodElement(psiFiles[1], "Baz", "getSibling");

        String expectedBeforeRenaming = "{{ myBaz.getSibling() }}";
        String expectedAfterRenaming = "{{ myBaz.getSomethingElse() }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElement(methodToRename, "getSomethingElse");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassMethod_renameTwigMethodCall_1() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_METHOD_1);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();
        PsiElement methodToRename = getMethodElement(psiFiles[1], "Baz", "getSibling");

        String expectedBeforeRenaming = "{{ myFoo.fooField.getSibling() }}";
        String expectedAfterRenaming = "{{ myFoo.fooField.getSomethingElse() }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElement(methodToRename, "getSomethingElse");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassMethod_renameTwigMethodCall_2() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_METHOD_2);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();
        PsiElement methodToRename = getMethodElement(psiFiles[1], "Baz", "getSibling");

        String expectedBeforeRenaming = "{{ myFoo.getSibling().getSibling() }}";
        String expectedAfterRenaming = "{{ myFoo.getSibling().getSomethingElse() }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElement(methodToRename, "getSomethingElse");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassMethod_renameTwigMethodCall_3() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_METHOD_3);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();
        PsiElement methodToRename = getMethodElement(psiFiles[1], "Baz", "getSibling");

        String expectedBeforeRenaming = "{{ myBaz.getSibling().getSibling() }}";
        String expectedAfterRenaming = "{{ myBaz.getSomethingElse().getSibling() }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElement(methodToRename, "getSomethingElse");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassMethod_renameTwigMethodCall_4() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_METHOD_4);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
        PsiElement methodToRename = getMethodElement(psiFiles[0], "Foo", "getSibling");

        String expectedBeforeRenaming = "{{ myBaz.setSibling(myFoo.getSibling()) }}";
        String expectedAfterRenaming = "{{ myBaz.setSibling(myFoo.getSomethingElse()) }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElement(methodToRename, "getSomethingElse");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassMethod_renameTwigMethodCall_5() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_METHOD_5);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
        PsiElement methodToRename = getMethodElement(psiFiles[0], "Foo", "getSibling");

        String expectedBeforeRenaming = "{{ myBaz.setSibling(myFoo.fooField.fooField.getSibling().getSibling()) }}";
        String expectedAfterRenaming = "{{ myBaz.setSibling(myFoo.fooField.fooField.getSomethingElse().getSibling()) }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElement(methodToRename, "getSomethingElse");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }

    @Test
    public void testRefactorPhpClassMethod_renameTwigMethodCall_6() {
        PsiFile[] psiFiles = myFixture.configureByFiles(FILE_PHP_FOO_CLASS, FILE_PHP_BAZ_CLASS, FILE_TEMPLATE_RENAME_METHOD_6);
        PsiElement variableUsage = psiFiles[2].getFirstChild().getNextSibling().getNextSibling();
        PsiElement methodToRename = getMethodElement(psiFiles[0], "Foo", "getSibling");

        String expectedBeforeRenaming = "{{ myBaz.getSibling().getSibling().getSibling().getSibling() }}";
        String expectedAfterRenaming = "{{ myBaz.getSibling().getSomethingElse().getSibling().getSomethingElse() }}";

        failIfNotEquals("initial expectation failed", expectedBeforeRenaming, variableUsage.getText());

        myFixture.renameElement(methodToRename, "getSomethingElse");

        assertEquals(expectedAfterRenaming, variableUsage.getText());
    }


    @NotNull
    private PsiElement getMethodElement(PsiFile psiFile, String className, @SuppressWarnings("SameParameterValue") String methodName) {
        PhpClass BazClass = PhpPsiUtil.findClass((PhpFile) psiFile, phpClass -> phpClass.getName().equals(className));
        assert BazClass != null;
        PsiElement methodToRename = BazClass.findOwnMethodByName(methodName);
        assert methodToRename != null;
        return methodToRename;
    }

    private void failIfNotEquals(@SuppressWarnings("SameParameterValue") @NotNull String message, @NotNull String expected, @NotNull String actual) {
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