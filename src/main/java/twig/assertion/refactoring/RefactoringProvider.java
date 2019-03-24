package twig.assertion.refactoring;

import com.intellij.packageDependencies.ui.RefactoringScopeElementListenerProvider;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;

class RefactoringProvider extends RefactoringScopeElementListenerProvider {
    @Override
    public RefactoringElementListener getListener(PsiElement element) {
        return RefactoringElementListenerFactory.create(element);
    }
}
