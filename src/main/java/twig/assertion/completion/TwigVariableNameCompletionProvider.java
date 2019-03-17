package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.jetbrains.twig.TwigTokenTypes;
import org.jetbrains.annotations.NotNull;
import twig.assertion.util.FindElements;

class TwigVariableNameCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        if (currElement.getPrevSibling().getText().equals(".")) {
            return;
        }
        if (twigElementIsIn(currElement, TwigTokenTypes.PRINT_BLOCK_START) || twigElementIsIn(currElement, TwigTokenTypes.STATEMENT_BLOCK_START)) {
            FindElements.findVariableNamesFromAsserts(currElement.getContainingFile()).forEach(s -> completionResultSet.addElement(LookupElementBuilder.create(s)));
        }
    }

    private boolean twigElementIsIn(PsiElement currElement, IElementType container) {
        if (currElement.getNode().getElementType() == container) {
            return true;
        }
        if (currElement.getPrevSibling() != null) {
            return twigElementIsIn(currElement.getPrevSibling(), container);
        }
        if (currElement.getParent() != null) {
            return twigElementIsIn(currElement.getParent(), container);
        }
        return false;
    }
}
