package twig.assertion.util;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.twig.elements.TwigCompositeElement;
import com.jetbrains.twig.util.TwigLookupUtil;
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

    static String findAssertTypeName(PsiFile file, String variableName) {
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
}
