package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpCompletionUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigTokenTypes;
import twig.assertion.util.ElementNavigator;
import twig.assertion.util.FindElements;
import twig.assertion.util.Fqn;

import java.util.ArrayList;
import java.util.Collection;

abstract class AbstractPhpClassMemberCompletionProvider extends CompletionProvider<CompletionParameters> {
    Collection<PhpClass> findPhpClassesForVariableLeftFromCaret(CompletionParameters parameters) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        if (currElement.getNode().getElementType() == TwigTokenTypes.IDENTIFIER && currElement.getPrevSibling().getText().equals(".")) {
            String variableName = new ElementNavigator(currElement).prev(2).getText();
            String phpClassnameTwigFormatted = FindElements.findAssertTypeName(parameters.getOriginalFile(), variableName);
            String phpClassname = Fqn.fromTwigString(phpClassnameTwigFormatted);
            PhpIndex phpIndex = PhpIndex.getInstance(currElement.getProject());
            PlainPrefixMatcher pm = new PlainPrefixMatcher(phpClassname);

            return PhpCompletionUtil.getAllClasses(pm, phpIndex);
        }

        return new ArrayList<>();
    }
}
