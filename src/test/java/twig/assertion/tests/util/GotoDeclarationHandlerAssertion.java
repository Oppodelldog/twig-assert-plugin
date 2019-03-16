package twig.assertion.tests.util;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import junit.framework.TestCase;
import twig.assertion.util.ElementNavigator;

import java.util.HashSet;
import java.util.Set;

public class GotoDeclarationHandlerAssertion {

    private final GotoDeclarationHandler handler;
    private final Editor editor;

    public GotoDeclarationHandlerAssertion(GotoDeclarationHandler handler, Editor editor) {
        this.handler = handler;
        this.editor = editor;
    }

    public void navigationIsEmpty(PsiElement psiElement) {

        PsiElement[] gotoDeclarationTargets = handler.getGotoDeclarationTargets(psiElement, 0, editor);

        if (gotoDeclarationTargets != null && gotoDeclarationTargets.length > 0) {
            TestCase.fail(String.format("expected to get no suggestions, but got %s", gotoDeclarationTargets.length));
        }
    }

    public void navigationContainsAssert(PsiElement psiElement, String expectedVariableName, String expectedType) {
        boolean found = false;
        PsiElement[] gotoDeclarationTargets = handler.getGotoDeclarationTargets(psiElement, 0, editor);
        if (gotoDeclarationTargets != null && gotoDeclarationTargets.length > 0) {
            for (PsiElement gotoDeclarationTarget : gotoDeclarationTargets) {
                ElementNavigator e = new ElementNavigator(gotoDeclarationTarget);
                if (!e.next(4).getText().equals(expectedVariableName)) {
                    continue;
                }
                if (!e.next(7).getText().equals(expectedType)) {
                    continue;
                }
                found = true;
            }
        }

        if (!found) {
            TestCase.fail(String.format("expected to find an assert (variable:%s, type:%s) as navigation target, but did not", expectedVariableName, expectedType));
        }
    }

    public void navigationContainsFqn(PsiElement psiElement, String targetShortcut) {
        Set<String> classTargets = new HashSet<>();

        PsiElement[] gotoDeclarationTargets = handler.getGotoDeclarationTargets(psiElement, 0, editor);
        if (gotoDeclarationTargets != null && gotoDeclarationTargets.length > 0) {
            for (PsiElement gotoDeclarationTarget : gotoDeclarationTargets) {
                classTargets.add(((PhpNamedElement) gotoDeclarationTarget).getFQN());
            }
        }

        if (!classTargets.contains(targetShortcut)) {
            TestCase.fail(String.format("expected to resolveAtCaret navigation target %s in navigation list %s, but did not.\nfurther information: navigation started at (%s) with text (%s)", targetShortcut, classTargets.toString(), psiElement.toString(), psiElement.getText()));
        }
    }
}