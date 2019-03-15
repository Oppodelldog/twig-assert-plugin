package twig.assertion.reference;

import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpCompletionUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import twig.assertion.completion.TwigAssertCompletionProvider;
import twig.assertion.util.ElementNavigator;
import twig.assertion.util.FindElements;

import java.util.ArrayList;

public class GotoPhpDeclarationHandler implements GotoDeclarationHandler {

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(PsiElement psiElement, int offset, Editor editor) {
        if (isCursorOnObjectAttribute(psiElement)) {
            return findPhpFieldsOrMethods(psiElement);
        } else if (isCursorOnFQCN(psiElement)) {
            return findPhpClass(psiElement);
        }

        return null;
    }

    private PsiElement[] findPhpClass(PsiElement psiElement) {
        String fullQualifiedClassName = psiElement.getText().replace("\\\\", "\\");
        PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());

        return phpIndex.getClassesByFQN(fullQualifiedClassName).toArray(new PsiElement[0]);
    }

    private boolean isCursorOnFQCN(@NotNull PsiElement psiElement) {
        ElementNavigator e = new ElementNavigator(psiElement);
        return e.prev(1).getNode().getElementType() == TwigTokenTypes.DOUBLE_QUOTE &&
                e.prev(2) instanceof PsiWhiteSpace &&
                e.prev(3).getNode().getElementType() == TwigTokenTypes.IDENTIFIER &&
                e.prev(4) instanceof PsiWhiteSpace &&
                e.prev(5).getNode().getText().equals(TwigAssertCompletionProvider.ASSERT_TAG_NAME);
    }

    @NotNull
    private PsiElement[] findPhpFieldsOrMethods(PsiElement psiElement) {
        String variableName = new ElementNavigator(psiElement).prev(2).getText();
        String phpClassname = FindElements.findAssertType(psiElement.getContainingFile(), variableName);
        String cursorElementName = psiElement.getText();
        PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());
        PlainPrefixMatcher pm = new PlainPrefixMatcher(phpClassname.replace("\\\\", "\\"));

        ArrayList<PsiElement> foundElements = new ArrayList<>();

        for (PhpClass phpClass : PhpCompletionUtil.getAllClasses(pm, phpIndex)) {
            phpClass.getMethods().stream().filter(method -> method.getName().equals(cursorElementName)).findFirst().ifPresent(foundElements::add);
            phpClass.getFields().stream().filter(field -> field.getName().equals(cursorElementName)).findFirst().ifPresent(foundElements::add);
        }

        return foundElements.toArray(new PsiElement[0]);
    }

    private boolean isCursorOnObjectAttribute(@NotNull PsiElement psiElement) {
        return psiElement.getNode() != null
                && psiElement.getNode().getElementType() == TwigTokenTypes.IDENTIFIER
                && psiElement.getPrevSibling() != null
                && psiElement.getPrevSibling().getText().equals(".");
    }

    @Nullable
    @Override
    public String getActionText(@NotNull DataContext context) {
        return null;
    }

}