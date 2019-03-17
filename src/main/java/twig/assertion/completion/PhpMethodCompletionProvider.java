package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.completion.PhpVariantsUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

class PhpMethodCompletionProvider extends AbstractPhpClassMemberCompletionProvider {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        PhpClass phpClass = findPhpClassForVariableLeftFromCaret(parameters);
        if (phpClass != null) {
            completionResultSet.addAllElements(PhpVariantsUtil.getLookupItems(phpClass.getMethods(), false, null));
        }
    }
}
