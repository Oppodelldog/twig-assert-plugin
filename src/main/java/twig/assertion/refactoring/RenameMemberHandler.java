package twig.assertion.refactoring;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigElementFactory;
import org.jetbrains.annotations.NotNull;
import twig.assertion.util.IdentifierHandler;

public class RenameMemberHandler implements IdentifierHandler {
    private final String newMemberName;
    private final String oldMemberName;
    private final String refactoredFqn;

    RenameMemberHandler(String newMemberName, String oldMemberName, String refactoredFqn) {
        this.newMemberName = newMemberName;
        this.oldMemberName = oldMemberName;
        this.refactoredFqn = refactoredFqn;
    }

    public PsiElement handleField(PsiElement element, PhpClass embeddingType) {
        if (isFieldToRename(element, embeddingType)) {
            return replaceTwigString(element.getProject(), element, this.newMemberName);
        }

        return element;
    }

    @Override
    public PsiElement handleMethod(PsiElement element, PhpClass embeddingType) {
        if (isMethodToRename(element, embeddingType)) {
            return replaceTwigString(element.getProject(), element, this.newMemberName);
        }

        return element;
    }

    private boolean isMemberToRename(PsiElement element, PhpClass statePrevPhpType) {
        String memberName = element.getText();
        boolean isRefactoredMember = memberName.equals(this.oldMemberName);
        boolean isPrevTypeRefactoredType = statePrevPhpType.getFQN().equals(this.refactoredFqn);

        return isRefactoredMember && isPrevTypeRefactoredType;
    }

    private boolean isMethodToRename(@NotNull PsiElement element, PhpClass statePrevPhpType) {
        boolean isRefactoredMemberInClass = (statePrevPhpType.findOwnMethodByName(this.newMemberName) != null);
        return isMemberToRename(element, statePrevPhpType) && isRefactoredMemberInClass;
    }

    private boolean isFieldToRename(@NotNull PsiElement element, PhpClass statePrevPhpType) {
        boolean isRefactoredMemberInClass = (statePrevPhpType.findOwnFieldByName(this.newMemberName, false) != null);
        return isMemberToRename(element, statePrevPhpType) && isRefactoredMemberInClass;
    }

    private PsiElement replaceTwigString(Project project, PsiElement assertElement, String replaceText) {
        String template = "{{ " + replaceText + " }}";
        final PsiElement[] newItem = {TwigElementFactory.createPsiElement(project, template, TwigTokenTypes.IDENTIFIER)};
        if (newItem[0] != null) {
            CommandProcessor.getInstance().executeCommand(project, () ->
                    ApplicationManager.getApplication().runWriteAction(() -> {

                        newItem[0] = assertElement.replace(newItem[0]);

                    }), "Refactor PHP Member Name", null);
        }

        return newItem[0];
    }
}
