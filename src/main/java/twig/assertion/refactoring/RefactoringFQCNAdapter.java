package twig.assertion.refactoring;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl;
import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigElementFactory;
import org.jetbrains.annotations.NotNull;
import twig.assertion.util.FindElements;
import twig.assertion.util.TwigFqn;

import static twig.assertion.completion.TwigAssertCompletionProvider.ASSERT_TAG_NAME;

class RefactoringFQCNAdapter extends com.intellij.refactoring.listeners.RefactoringElementAdapter {
    private final String originalFqn;

    RefactoringFQCNAdapter(PhpClassImpl originalElement) {
        this.originalFqn = originalElement.getFQN();
    }

    private static void replaceTwigString(Project project, PsiElement assertElement, String replaceText) {
        String template = "{{ set x = \"" + replaceText + "\" }}";
        PsiElement newItem = TwigElementFactory.createPsiElement(project, template, TwigTokenTypes.STRING_TEXT);
        if (newItem != null) {
            assertElement.replace(newItem);
        }
    }

    @Override
    protected void elementRenamedOrMoved(@NotNull PsiElement psiElement) {
        if (psiElement instanceof PhpClass) {
            final Project project = psiElement.getProject();
            String previousFQCN = TwigFqn.toTwigString(this.originalFqn);
            String replaceText = TwigFqn.toTwigString(((PhpClass) psiElement).getFQN());
            GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
            PsiSearchHelper psiSearchHelper = PsiSearchHelper.getInstance(project);
            psiSearchHelper.processAllFilesWithWordInLiterals(ASSERT_TAG_NAME, searchScope, psiFile -> {
                if (psiFile.getLanguage() == TwigLanguage.INSTANCE) {
                    FindElements.findAssertPsiElementsByFQCN(psiFile, previousFQCN).
                            forEach(assertElement
                                    -> CommandProcessor.getInstance().executeCommand(project, ()
                                    -> ApplicationManager.getApplication().runWriteAction(()
                                    -> replaceTwigString(project, assertElement, replaceText)
                            ), "Refactor PHP Class", null));
                }
                return true;
            });
        }
    }

    @Override
    public void undoElementMovedOrRenamed(@NotNull PsiElement psiElement, @NotNull String s) {
    }
}