package twig.assertion.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.twig.TwigTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import twig.assertion.completion.TwigAssertCompletionProvider;
import twig.assertion.util.ElementNavigator;
import twig.assertion.util.FindElements;
import twig.assertion.util.Fqn;

import static com.jetbrains.twig.TwigTokenTypes.IDENTIFIER;

public class GotoPhpDeclarationHandler implements GotoDeclarationHandler {

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(PsiElement psiElement, int offset, Editor editor) {
        if (psiElement == null) {
            return null;
        }
        if (isCursorOnFQCN(psiElement)) {
            return findClass(psiElement);
        }

        PsiElement accessOrigin = TwigAccessOriginFinder.getOriginOfMemberTree(psiElement);
        if ((accessOrigin) == null) {
            return null;
        }

        if (isElementOrigin(psiElement, accessOrigin)) {
            return new PsiElement[]{FindElements.findAssertPsiElement(psiElement.getContainingFile(), psiElement.getText())};
        }

        PhpClassMemberResolver resolver = new PhpClassMemberResolver(psiElement, accessOrigin);
        resolver.resolve();
        if (resolver.hasResolvedPsiElement()) {
            return new PsiElement[]{resolver.getResolvedPsiElement()};
        }
        return new PsiElement[]{};
    }

    private boolean isElementOrigin(PsiElement psiElement, PsiElement accessOrigin) {
        return accessOrigin.equals(psiElement) || (accessOrigin.getFirstChild() != null && accessOrigin.getFirstChild().equals(psiElement));
    }

    private PsiElement[] findClass(PsiElement psiElement) {
        String fullQualifiedClassName = Fqn.fromTwigString(psiElement.getText());
        PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());

        return phpIndex.getClassesByFQN(fullQualifiedClassName).toArray(new PsiElement[0]);
    }

    private boolean isCursorOnFQCN(PsiElement psiElement) {
        try {
            ElementNavigator e = new ElementNavigator(psiElement);
            return e.prev(1).getNode().getElementType() == TwigTokenTypes.DOUBLE_QUOTE &&
                    e.prev(2) instanceof PsiWhiteSpace &&
                    e.prev(3).getNode().getElementType() == IDENTIFIER &&
                    e.prev(4) instanceof PsiWhiteSpace &&
                    e.prev(5).getNode().getText().equals(TwigAssertCompletionProvider.ASSERT_TAG_NAME);
        } catch (Exception e) {
            return false;
        }
    }

    @Nullable
    @Override
    public String getActionText(@NotNull DataContext context) {
        return null;
    }
}