package twig.assertion.util;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigCompositeElement;
import com.jetbrains.twig.elements.TwigElementTypes;
import com.jetbrains.twig.util.TwigLookupUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.jetbrains.twig.TwigTokenTypes.TAG_NAME;
import static twig.assertion.completion.TwigAssertCompletionProvider.ASSERT_TAG_NAME;

public class FindElements {

    private static final Condition<PsiElement> conditionAssertTag = (element) -> {
        if (element instanceof TwigCompositeElement) {
            PsiElementAccessor constraints = new PsiElementAccessor(element.getFirstChild());
            return constraints.nextInstanceOf(1, PsiWhiteSpace.class) &&
                    constraints.nextElementTypeOf(2, TAG_NAME) &&
                    constraints.nextElementTextEquals(2, ASSERT_TAG_NAME);
        }

        return false;
    };

    private static Condition<PsiElement> conditionObjectAccess(String variableName) {
        return element -> {
            if (element.getText().equals(variableName) && element.getContext() != null && element.getNextSibling() != null) {
                IElementType contextElementType = element.getContext().getNode().getElementType();
                if (element.getParent().getNode().getElementType() == TwigElementTypes.METHOD_CALL ||
                        element.getParent().getNode().getElementType() == TwigElementTypes.FUNCTION_CALL) {
                    assert element.getParent().getContext() != null;
                    contextElementType = element.getParent().getContext().getNode().getElementType();
                }
                return ((contextElementType == TwigElementTypes.TAG ||
                        contextElementType == TwigElementTypes.PRINT_BLOCK ||
                        contextElementType == TwigElementTypes.IF_TAG ||
                        contextElementType == TwigElementTypes.METHOD_CALL ||
                        contextElementType == TwigElementTypes.FUNCTION_CALL)
                        && element.getNextSibling().getNode().getElementType() == TwigTokenTypes.DOT
                );
            }
            return false;
        };
    }

    public static String findAssertTypeName(PsiFile file, String variableName) {
        ArrayList<PsiElement> elements = findAssertsInFile(file);
        for (PsiElement element :
                elements) {
            PsiElementAccessor constraints = new PsiElementAccessor(element.getFirstChild());
            if (constraints.nextElementTextEquals(4, variableName)) {
                return constraints.getNextText(7);
            }
        }

        return "";
    }

    @Nullable
    public static PsiElement findAssertPsiElement(PsiFile file, String variableName) {
        ArrayList<PsiElement> elements = findAssertsInFile(file);
        for (PsiElement element :
                elements) {
            PsiElementAccessor constraints = new PsiElementAccessor(element.getFirstChild());
            if (constraints.nextElementTextEquals(4, variableName)) {
                return constraints.getPrevious(0).orElse(null);
            }
        }

        return null;
    }

    public static ArrayList<PsiElement> findAllAssertPsiElements(PsiFile file) {
        return findAssertElements(file, "");
    }

    public static ArrayList<PsiElement> findAssertPsiElementsByFQCN(PsiFile file, String fqcn) {
        return findAssertElements(file, fqcn);
    }

    @NotNull
    private static ArrayList<PsiElement> findAssertElements(PsiFile file, String searchedFQN) {
        ArrayList<PsiElement> matchingElements = new ArrayList<>();
        ArrayList<PsiElement> elements = findAssertsInFile(file);
        for (PsiElement element : elements) {
            PsiElementAccessor constraints = new PsiElementAccessor(element.getFirstChild());
            if (constraints.nextElementTextEquals(7, searchedFQN) || searchedFQN.isEmpty()) {
                constraints.getNext(7).ifPresent(matchingElements::add);
            }
        }

        return matchingElements;
    }

    public static ArrayList<String> findVariableNamesFromAsserts(PsiFile file) {
        final ArrayList<String> variableNames = new ArrayList<>();
        findAssertsInFile(file).forEach(psiElement -> new PsiElementAccessor(psiElement.getFirstChild()).getNext(4).ifPresent(psiElement1 -> variableNames.add(psiElement1.getText())));

        return variableNames;
    }

    private static ArrayList<PsiElement> findAssertsInFile(PsiFile file) {
        final ArrayList<PsiElement> foundElements = new ArrayList<>();
        TwigLookupUtil.ElementFinder finder = new TwigLookupUtil.ElementFinder(FindElements.conditionAssertTag) {

            public boolean handleMatch(PsiElement element) {
                foundElements.add(element);
                return false;
            }
        };

        finder.visitFile(file);

        return foundElements;
    }

    public static ArrayList<PsiElement> findObjectAccessesInFile(PsiFile psiFile, String variableName) {
        final ArrayList<PsiElement> result = new ArrayList<>();
        TwigLookupUtil.ElementFinder finder = new TwigLookupUtil.ElementFinder(conditionObjectAccess(variableName)) {

            public boolean handleMatch(PsiElement element) {
                result.add(element);
                return false;
            }
        };

        finder.visitFile(psiFile);


        return result;
    }
}
