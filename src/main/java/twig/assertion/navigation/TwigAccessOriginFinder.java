package twig.assertion.navigation;

import com.intellij.psi.PsiElement;

import static com.jetbrains.twig.TwigTokenTypes.IDENTIFIER;
import static com.jetbrains.twig.elements.TwigElementTypes.METHOD_CALL;

public class TwigAccessOriginFinder {

    public static PsiElement getOriginOfMemberTree(PsiElement psiElement) {
        boolean navPossible;
        while ((navPossible = isNavigationPossible(psiElement)) && !foundOrigin(psiElement)) {
            if (psiElement.getPrevSibling() == null) {
                psiElement = psiElement.getParent();
            } else {
                psiElement = psiElement.getPrevSibling();
            }
        }

        if (!navPossible) {
            return null;
        }

        return psiElement;
    }

    private static boolean foundOrigin(PsiElement psiElement) {
        if (psiElement == null)
            return false;

        if (psiElement.getNode() == null || (psiElement.getNode().getElementType() != METHOD_CALL && psiElement.getNode().getElementType() != IDENTIFIER)) {
            return false;
        }

        return psiElement.getPrevSibling() != null && !psiElement.getPrevSibling().getText().equals(".");
    }

    private static boolean isNavigationPossible(PsiElement psiElement) {
        if (psiElement == null)
            return false;

        return (psiElement.getPrevSibling() != null || psiElement.getParent() != null);
    }
}
