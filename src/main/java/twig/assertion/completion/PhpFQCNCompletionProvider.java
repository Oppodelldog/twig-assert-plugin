package twig.assertion.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.text.StringUtil;
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
import twig.assertion.util.ElementNavigator;

class PhpFQCNCompletionProvider extends CompletionProvider<CompletionParameters> {


    PhpFQCNCompletionProvider() {
    }

    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        ElementNavigator e = new ElementNavigator(parameters.getPosition().getOriginalElement());

        if (e.prev(1) instanceof PsiWhiteSpace &&
                e.prev(2).getNode().getElementType() == TwigTokenTypes.IDENTIFIER &&
                e.prev(3) instanceof PsiWhiteSpace &&
                e.prev(4).getNode().getElementType() == TwigTokenTypes.TAG_NAME &&
                e.prev(4).getNode().getText().equals(TwigAssertCompletionProvider.ASSERT_TAG_NAME)
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
                    namespaceFQN = StringUtil.replace(namespaceFQN, "\\", "\\\\");
                    context.getDocument().insertString(context.getStartOffset(), "\"" + namespaceFQN);
                } finally {
                    PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getDocument());
                }
            }

        }
    }
}