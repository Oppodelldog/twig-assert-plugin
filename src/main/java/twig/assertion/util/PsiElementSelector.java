package twig.assertion.util;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PsiElementSelector {
    private final PsiElement e;

    PsiElementSelector(@NotNull PsiElement e) {
        this.e = e;
    }

    @Nullable
    PsiElement prev(int i) {
        PsiElement prev = this.e;

        for (int j = 0; j < i; j++) {
            prev = prev.getPrevSibling();
            if (prev == null) {
                return null;
            }
        }

        return prev;
    }

    @Nullable
    PsiElement next(int i) {
        PsiElement next = this.e;

        for (int j = 0; j < i; j++) {
            next = next.getNextSibling();
            if (next == null) {
                return null;
            }
        }

        return next;
    }
}

