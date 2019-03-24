package twig.assertion.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PsiElementAccessor {
    private final PsiElementSelector navigator;

    public PsiElementAccessor(@NotNull PsiElement element) {
        this.navigator = new PsiElementSelector(element);
    }

    public boolean prevInstanceOf(int i, Class<?> t) {
        PsiElement e;
        if ((e = navigator.prev(i)) != null) {
            return t.isInstance(e);
        }
        return false;
    }

    @SuppressWarnings("SameParameterValue")
    boolean nextInstanceOf(int i, Class<?> t) {
        PsiElement e;
        if ((e = navigator.next(i)) != null) {
            return t.isInstance(e);
        }
        return false;
    }

    public boolean prevElementTypeOf(int i, IElementType t) {
        PsiElement e;
        if ((e = navigator.prev(i)) != null) {
            return e.getNode().getElementType() == t;
        }
        return false;
    }

    public boolean nextElementTypeOf(int i, IElementType t) {
        PsiElement e;
        if ((e = navigator.next(i)) != null) {
            return e.getNode().getElementType() == t;
        }
        return false;
    }

    public boolean prevElementTextEquals(int i, String text) {
        PsiElement e;
        if ((e = navigator.prev(i)) != null) {
            return e.getNode().getText().equals(text);
        }
        return false;
    }

    public boolean nextElementTextEquals(int i, String text) {
        PsiElement e;
        if ((e = navigator.next(i)) != null) {
            return e.getNode().getText().equals(text);
        }
        return false;
    }

    public String getNextText(int i) {
        PsiElement e;
        if ((e = navigator.next(i)) != null) {
            return e.getText();
        }
        return "";
    }

    public Optional<PsiElement> getPrevious(int i) {
        return Optional.ofNullable(navigator.prev(i));
    }

    public Optional<PsiElement> getNext(int i) {
        return Optional.ofNullable(navigator.next(i));
    }

}
