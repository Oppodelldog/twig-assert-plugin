package twig.assertion.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl;

class RefactoringElementListenerFactory {
    static RefactoringElementListener create(PsiElement element) {
        if (element instanceof PhpClassImpl) {
            return RefactoringFQCNAdapterFactory.create((PhpClassImpl) element);
        }
        if (element instanceof PhpClassMember) {
            try {
                return RefactoringClassMemberAdapterFactory.create((PhpClassMember) element);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
