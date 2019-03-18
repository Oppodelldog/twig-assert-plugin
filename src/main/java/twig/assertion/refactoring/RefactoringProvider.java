package twig.assertion.refactoring;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.packageDependencies.ui.RefactoringScopeElementListenerProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerComposite;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigElementFactory;
import org.jetbrains.annotations.NotNull;
import twig.assertion.util.FindElements;
import twig.assertion.util.TwigFqn;

import static twig.assertion.completion.TwigAssertCompletionProvider.ASSERT_TAG_NAME;

class RefactoringProvider extends RefactoringScopeElementListenerProvider {
    @Override
    public RefactoringElementListener getListener(PsiElement element) {
        RefactoringElementListenerComposite composite = new RefactoringElementListenerComposite();

        composite.addListener(new RefactoringFQCNAdapter(element));

        return composite;
    }

    static class RefactoringFQCNAdapter extends com.intellij.refactoring.listeners.RefactoringElementAdapter {
        private String originalFqn = "";

        RefactoringFQCNAdapter(PsiElement originalElement) {
            if (originalElement instanceof PhpNamedElement) {
                this.originalFqn = ((PhpNamedElement) originalElement).getFQN();
            }
        }

        @Override
        protected void elementRenamedOrMoved(@NotNull PsiElement psiElement) {
            if (psiElement instanceof PhpClass) {
                String previousFQCN = TwigFqn.toTwigString(this.originalFqn);
                String replaceWith = TwigFqn.toTwigString(((PhpClass) psiElement).getFQN());
                final Project project = psiElement.getProject();
                GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);

                PsiSearchHelper.getInstance(project).processAllFilesWithWordInLiterals(ASSERT_TAG_NAME, searchScope, psiFile -> {
                    if (psiFile.getLanguage() == TwigLanguage.INSTANCE) {
                        FindElements.findAssertPsiElementsByFQCN(psiFile, previousFQCN).forEach(assertElement -> {
                            CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
                                PsiElement newItem = TwigElementFactory.createPsiElement(project, "{{ set x = \"" + replaceWith + "\" }}", TwigTokenTypes.STRING_TEXT);
                                if (newItem != null) {
                                    assertElement.replace(newItem);
                                }
                            }), "Refactor PHP Class", null);
                        });
                    }
                    return true;
                });
            }
        }

        @Override
        public void undoElementMovedOrRenamed(@NotNull PsiElement psiElement, @NotNull String s) {
        }
    }
}
