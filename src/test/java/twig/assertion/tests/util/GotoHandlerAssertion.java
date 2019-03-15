package twig.assertion.tests.util;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class GotoHandlerAssertion {

    private final GotoDeclarationHandler handler;
    private final CodeInsightTestFixture fixture;

    public GotoHandlerAssertion(GotoDeclarationHandler handler, CodeInsightTestFixture fixture) {
        this.handler = handler;
        this.fixture = fixture;
    }

    public void navigationContains(LanguageFileType languageFileType, String sourceCode, String expectedTargetFQN) {
        fixture.configureByText(languageFileType, sourceCode);
        PsiElement psiElement = fixture.getFile().findElementAt(fixture.getCaretOffset());
        assertNavigationContains(psiElement, expectedTargetFQN);
    }

    private void assertNavigationContains(PsiElement psiElement, String targetShortcut) {
        Set<String> classTargets = new HashSet<>();

        PsiElement[] gotoDeclarationTargets = handler.getGotoDeclarationTargets(psiElement, 0, fixture.getEditor());
        if (gotoDeclarationTargets != null && gotoDeclarationTargets.length > 0) {
            for (PsiElement gotoDeclarationTarget : gotoDeclarationTargets) {
                classTargets.add(((PhpNamedElement) gotoDeclarationTarget).getFQN());
            }

            if (!classTargets.contains(targetShortcut)) {
                TestCase.fail(String.format("expected to find navigation target %s in navigation list %s, but did not.\nfurther information: navigation started at (%s) with text (%s)", targetShortcut, classTargets.toString(), psiElement.toString(), psiElement.getText()));
            }
        }
    }
}