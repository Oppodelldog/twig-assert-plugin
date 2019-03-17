package twig.assertion.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpClassLookupElement;
import com.jetbrains.php.completion.PhpCompletionUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigTokenTypes;
import org.jetbrains.annotations.NotNull;
import twig.assertion.util.PsiElementAccessor;
import twig.assertion.util.TwigFqn;

class PhpFQCNCompletionProvider extends CompletionProvider<CompletionParameters> {


    PhpFQCNCompletionProvider() {
    }

    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        PsiElementAccessor constraints = new PsiElementAccessor(parameters.getPosition().getOriginalElement());

        if (constraints.prevInstanceOf(1, PsiWhiteSpace.class) &&
                constraints.prevElementTypeOf(2, TwigTokenTypes.IDENTIFIER) &&
                constraints.prevInstanceOf(3, PsiWhiteSpace.class) &&
                constraints.prevElementTypeOf(4, TwigTokenTypes.TAG_NAME) &&
                constraints.prevElementTextEquals(4, TwigAssertCompletionProvider.ASSERT_TAG_NAME)
        ) {
            PhpIndex phpIndex = PhpIndex.getInstance(currElement.getProject());
            PhpCompletionUtil.getAllClasses(PrefixMatcher.ALWAYS_TRUE, phpIndex).forEach(
                    phpClass -> result.addElement(new PhpClassLookupElement(phpClass, false, PhpClassFullNameInsertHandler.INSTANCE)));
        }
    }


    private static class PhpClassFullNameInsertHandler implements InsertHandler<LookupElement> {
        private static final InsertHandler<LookupElement> INSTANCE = new PhpClassFullNameInsertHandler();

        private PhpClassFullNameInsertHandler() {
        }

        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {

            Object object = item.getObject();
            if (object instanceof PhpClass) {
                try {
                    String namespaceFQN = ((PhpClass) object).getNamespaceName();
                    namespaceFQN = TwigFqn.toTwigString(namespaceFQN);
                    context.getDocument().insertString(context.getStartOffset(), "\"" + namespaceFQN);
                } finally {
                    PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getDocument());
                }
            }

        }
    }
}