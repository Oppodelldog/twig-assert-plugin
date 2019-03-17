package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigTokenTypes;
import twig.assertion.util.PhpClassMemberResolver;
import twig.assertion.util.PsiElementAccessor;
import twig.assertion.util.TwigAccessOriginFinder;

abstract class AbstractPhpClassMemberCompletionProvider extends CompletionProvider<CompletionParameters> {
    PhpClass findPhpClassForVariableLeftFromCaret(CompletionParameters parameters) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        if (currElement.getNode().getElementType() == TwigTokenTypes.IDENTIFIER && currElement.getPrevSibling().getText().equals(".")) {

            PsiElementAccessor nav = new PsiElementAccessor(currElement);
            PsiElement selectedElement = nav.getPrevious(2).orElse(null);
            if (selectedElement == null) {
                return null;
            }
            PsiElement origin = TwigAccessOriginFinder.getOriginOfMemberTree(selectedElement);
            PhpClassMemberResolver phpMemberResolver = new PhpClassMemberResolver(selectedElement, origin);
            phpMemberResolver.resolve();
            return phpMemberResolver.getResolvedPhpClass();
        }

        return null;
    }
}
