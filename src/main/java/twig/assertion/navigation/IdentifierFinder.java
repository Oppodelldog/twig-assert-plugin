package twig.assertion.navigation;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;
import twig.assertion.util.IdentifierHandler;

public class IdentifierFinder implements IdentifierHandler {

    private final PsiElement targetElement;
    private PhpNamedElement foundElement;

    public IdentifierFinder(PsiElement targetElement) {
        this.targetElement = targetElement;
        foundElement = null;
    }

    public boolean hasFoundElement() {
        return foundElement != null;
    }

    public PhpNamedElement getFoundElement() {
        return foundElement;
    }

    @Override
    public PsiElement handleMethod(PsiElement element, PhpClass embeddingType) {
        if (reachedResolvingTarget(element, targetElement) && this.foundElement == null) {
            this.foundElement = embeddingType.findOwnMethodByName(element.getText());
            return null;
        }
        return element;
    }

    @Override
    public PsiElement handleField(PsiElement element, PhpClass embeddingType) {
        if (reachedResolvingTarget(element, targetElement) && this.foundElement == null) {
            this.foundElement = embeddingType.findOwnFieldByName(element.getText(), false);
            return null;
        }
        return element;
    }


    private boolean reachedResolvingTarget(@NotNull PsiElement current, PsiElement psiElement) {
        if (current.equals(psiElement)) {
            return true;
        }

        current = current.getFirstChild();
        if (current == null) {
            return false;
        }

        return false;
    }
}
