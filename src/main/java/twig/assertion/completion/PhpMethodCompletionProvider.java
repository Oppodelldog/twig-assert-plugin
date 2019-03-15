package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.completion.PhpVariantsUtil;
import org.jetbrains.annotations.NotNull;

class PhpMethodCompletionProvider extends AbstractPhpClassMemberCompletionProvider {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        findPhpClassesForVariableLeftFromCaret(parameters).forEach(phpClass -> completionResultSet.addAllElements(PhpVariantsUtil.getLookupItems(phpClass.getMethods(), false, null)));
    }
}
