package twig.assertion.util;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;

public interface IdentifierHandler {
    PsiElement handleField(PsiElement element, PhpClass embeddingType);

    PsiElement handleMethod(PsiElement element, PhpClass embeddingType);
}
