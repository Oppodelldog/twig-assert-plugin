package twig.assertion.tests.util;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class GotoHandlerAssertion {

    private final GotoDeclarationHandler handler;
    private final Editor editor;

    public GotoHandlerAssertion(GotoDeclarationHandler handler, Editor editor) {
        this.handler = handler;
        this.editor = editor;
    }

    public void navigationContains(PsiElement psiElement, String targetShortcut) {
        Set<String> classTargets = new HashSet<>();

        PsiElement[] gotoDeclarationTargets = handler.getGotoDeclarationTargets(psiElement, 0, editor);
        if (gotoDeclarationTargets != null && gotoDeclarationTargets.length > 0) {
            for (PsiElement gotoDeclarationTarget : gotoDeclarationTargets) {
                classTargets.add(((PhpNamedElement) gotoDeclarationTarget).getFQN());
            }

            if (!classTargets.contains(targetShortcut)) {
                TestCase.fail(String.format("expected to resolveAtCaret navigation target %s in navigation list %s, but did not.\nfurther information: navigation started at (%s) with text (%s)", targetShortcut, classTargets.toString(), psiElement.toString(), psiElement.getText()));
            }
        }
    }
}