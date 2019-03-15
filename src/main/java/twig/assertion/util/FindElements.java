package twig.assertion.util;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigCompositeElement;
import com.jetbrains.twig.util.TwigLookupUtil;
import twig.assertion.completion.TwigAssertCompletionProvider;

import java.util.ArrayList;

public class FindElements {

    private static final Condition<PsiElement> conditionAssertTag = (element) -> {
        if (element instanceof TwigCompositeElement) {
            ElementNavigator e = new ElementNavigator(element.getFirstChild());
            return e.next(1) instanceof PsiWhiteSpace &&
                    e.next(2).getNode().getElementType() == TwigTokenTypes.TAG_NAME &&
                    e.next(2).getText().equals(TwigAssertCompletionProvider.ASSERT_TAG_NAME);
        }

        return false;
    };

    public static String findAssertType(PsiFile file, String variableName) {
        ArrayList<PsiElement> elements = findElementsInFile(file);
        for (PsiElement element :
                elements) {
            ElementNavigator e = new ElementNavigator(element.getFirstChild());
            if (e.next(4).getText().equals(variableName)) {
                return e.next(7).getText();
            }
        }

        return "";
    }

    private static ArrayList<PsiElement> findElementsInFile(PsiFile file) {
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
