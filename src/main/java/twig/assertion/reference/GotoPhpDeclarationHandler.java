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
import twig.assertion.util.Fqn;

import java.util.ArrayList;

public class GotoPhpDeclarationHandler implements GotoDeclarationHandler {

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(PsiElement psiElement, int offset, Editor editor) {
        if (psiElement == null) {
            return null;
        }

        if (isCaretOnObjectMember(psiElement)) {
            return findClassMembers(psiElement);
        } else if (isCursorOnFQCN(psiElement)) {
            return findClass(psiElement);
        }

        return null;
    }

    private PsiElement[] findClass(PsiElement psiElement) {
        String fullQualifiedClassName = Fqn.fromTwigString(psiElement.getText());
        PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());

        return phpIndex.getClassesByFQN(fullQualifiedClassName).toArray(new PsiElement[0]);
    }

    private boolean isCursorOnFQCN(PsiElement psiElement) {
        ElementNavigator e = new ElementNavigator(psiElement);
        return e.prev(1).getNode().getElementType() == TwigTokenTypes.DOUBLE_QUOTE &&
                e.prev(2) instanceof PsiWhiteSpace &&
                e.prev(3).getNode().getElementType() == TwigTokenTypes.IDENTIFIER &&
                e.prev(4) instanceof PsiWhiteSpace &&
                e.prev(5).getNode().getText().equals(TwigAssertCompletionProvider.ASSERT_TAG_NAME);
    }

    private PsiElement[] findClassMembers(PsiElement psiElement) {
        PlainPrefixMatcher pm = getPlainPrefixMatcherFromAssertedType(psiElement);
        String cursorElementName = psiElement.getText();

        ArrayList<PsiElement> foundElements = new ArrayList<>();
        PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());
        for (PhpClass phpClass : PhpCompletionUtil.getAllClasses(pm, phpIndex)) {
            phpClass.getMethods().stream().filter(method -> method.getName().equals(cursorElementName)).findFirst().ifPresent(foundElements::add);
            phpClass.getFields().stream().filter(field -> field.getName().equals(cursorElementName)).findFirst().ifPresent(foundElements::add);
        }

        return foundElements.toArray(new PsiElement[0]);
    }

    @NotNull
    private PlainPrefixMatcher getPlainPrefixMatcherFromAssertedType(PsiElement psiElement) {
        String classNameTwigFormatted = FindElements.findAssertType(psiElement.getContainingFile(), psiElement);
        String className = Fqn.fromTwigString(classNameTwigFormatted);

        return new PlainPrefixMatcher(className);
    }

    private boolean isCaretOnObjectMember(PsiElement psiElement) {
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