package twig.assertion.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import twig.assertion.completion.TwigAssertCompletionProvider;
import twig.assertion.util.*;

import java.util.Optional;

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


        String variableName = accessOrigin.getNode().getText();
        if (accessOrigin.getNode().getElementType() == TwigElementTypes.METHOD_CALL) {
            variableName = accessOrigin.getFirstChild().getText();
        }
        PsiElement assertElement = FindElements.findAssertPsiElement(accessOrigin.getContainingFile(), variableName);
        if (assertElement == null) {
            return new PsiElement[]{};
        }
        PsiElementAccessor accessor = new PsiElementAccessor(assertElement);
        Optional<PsiElement> typeFQCN = accessor.getNext(7);
        if (!typeFQCN.isPresent()) {
            return new PsiElement[]{};
        }
        String originElementFQCN = TwigFqn.fromTwigString(typeFQCN.get().getText());

        IdentifierFinder catcher = new IdentifierFinder(psiElement);
        PhpIndex phpIndex = PhpIndex.getInstance(accessOrigin.getProject());
        Optional<PhpClass> first = phpIndex.getClassesByFQN(originElementFQCN).stream().findFirst();
        if (!first.isPresent()) {
            return new PsiElement[]{};
        }

        PsiElement objectVariableElement = accessOrigin;
        if (accessOrigin.getNode().getElementType() == TwigElementTypes.METHOD_CALL) {
            objectVariableElement = accessOrigin.getFirstChild();
        }
        TwigPhpIdentifierVisitor visitor = new TwigPhpIdentifierVisitor(first.get(), phpIndex, catcher, objectVariableElement);
        visitor.visitElement(accessOrigin);

        if (catcher.hasFoundElement()) {
            return new PsiElement[]{catcher.getFoundElement()};
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