package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpCompletionUtil;
import com.jetbrains.php.completion.PhpVariantsUtil;
import com.jetbrains.twig.TwigTokenTypes;
import org.jetbrains.annotations.NotNull;
import twig.assertion.util.ElementNavigator;
import twig.assertion.util.FindElements;

public class PhpMethodCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        if (currElement.getNode().getElementType() == TwigTokenTypes.IDENTIFIER && currElement.getPrevSibling().getText().equals(".")) {
            String variableName = new ElementNavigator(currElement).prev(2).getText();
            String phpClassname = FindElements.findAssertType(parameters.getOriginalFile(), variableName);
            PhpIndex phpIndex = PhpIndex.getInstance(currElement.getProject());
            PlainPrefixMatcher pm = new PlainPrefixMatcher(phpClassname.replace("\\\\", "\\"));

            PhpCompletionUtil.getAllClasses(pm, phpIndex).forEach(phpClass -> completionResultSet.addAllElements(PhpVariantsUtil.getLookupItems(phpClass.getMethods(), false, null)));
        }
    }
}
