package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpCompletionUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigTokenTypes;
import twig.assertion.util.FindElements;

import java.util.ArrayList;
import java.util.Collection;

abstract class AbstractPhpClassMemberCompletionProvider extends CompletionProvider<CompletionParameters> {
    Collection<PhpClass> findPhpClassesForVariableLeftFromCaret(CompletionParameters parameters) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        if (currElement.getNode().getElementType() == TwigTokenTypes.IDENTIFIER && currElement.getPrevSibling().getText().equals(".")) {
            String phpClassname = FindElements.findAssertType(parameters.getOriginalFile(), currElement);
            PhpIndex phpIndex = PhpIndex.getInstance(currElement.getProject());
            PlainPrefixMatcher pm = new PlainPrefixMatcher(phpClassname.replace("\\\\", "\\"));

            return PhpCompletionUtil.getAllClasses(pm, phpIndex);
        }

        return new ArrayList<PhpClass>();
    }
}
