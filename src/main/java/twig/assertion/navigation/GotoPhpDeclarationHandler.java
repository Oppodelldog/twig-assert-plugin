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
import twig.assertion.util.*;

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

    private boolean isElementOrigin(@NotNull PsiElement psiElement, @NotNull PsiElement accessOrigin) {
        return accessOrigin.equals(psiElement) || (accessOrigin.getFirstChild() != null && accessOrigin.getFirstChild().equals(psiElement));
    }

    @NotNull
    private PsiElement[] findClass(@NotNull PsiElement psiElement) {
        String fullQualifiedClassName = TwigFqn.fromTwigString(psiElement.getText());
        PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());

        return phpIndex.getClassesByFQN(fullQualifiedClassName).toArray(new PsiElement[0]);
    }

    private boolean isCursorOnFQCN(@NotNull PsiElement psiElement) {

        PsiElementAccessor constraints = new PsiElementAccessor(psiElement);

        return constraints.prevElementTypeOf(1, TwigTokenTypes.DOUBLE_QUOTE) &&
                constraints.prevInstanceOf(2, PsiWhiteSpace.class) &&
                constraints.prevElementTypeOf(3, IDENTIFIER) &&
                constraints.prevInstanceOf(4, PsiWhiteSpace.class) &&
                constraints.prevElementTextEquals(5, TwigAssertCompletionProvider.ASSERT_TAG_NAME);

    }

    @Nullable
    @Override
    public String getActionText(@NotNull DataContext context) {
        return null;
    }
}