package twig.assertion.util;

import com.intellij.psi.PsiElement;

public class ElementNavigator {
    private final PsiElement e;

    public ElementNavigator(PsiElement e) {
        this.e = e;
    }

    public PsiElement prev(int i) {
        PsiElement prev = this.e;

        for (int j = 0; j < i; j++) {
            prev = prev.getPrevSibling();
        }

        return prev;
    }

    public PsiElement next(int i) {
        PsiElement next = this.e;

        for (int j = 0; j < i; j++) {
            next = next.getNextSibling();
        }

        return next;
    }
}

