package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.twig.TwigTokenTypes;
import org.jetbrains.annotations.NotNull;

public class TwigAssertCompletionProvider extends CompletionProvider<CompletionParameters> {
    public static final String ASSERT_TAG_NAME = "assert";

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        if (currElement.getNode().getElementType() == TwigTokenTypes.TAG_NAME) {
            completionResultSet.addElement(LookupElementBuilder.create(TwigAssertCompletionProvider.ASSERT_TAG_NAME));
        }
    }
}

