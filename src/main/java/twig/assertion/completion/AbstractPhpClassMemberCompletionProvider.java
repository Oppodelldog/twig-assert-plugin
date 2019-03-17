package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigTokenTypes;
import twig.assertion.navigation.PhpClassMemberResolver;
import twig.assertion.navigation.TwigAccessOriginFinder;
import twig.assertion.util.ElementNavigator;

abstract class AbstractPhpClassMemberCompletionProvider extends CompletionProvider<CompletionParameters> {
    PhpClass findPhpClassForVariableLeftFromCaret(CompletionParameters parameters) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        if (currElement.getNode().getElementType() == TwigTokenTypes.IDENTIFIER && currElement.getPrevSibling().getText().equals(".")) {

            ElementNavigator nav = new ElementNavigator(currElement);
            PsiElement selectedElement = nav.prev(2);
            PsiElement origin = TwigAccessOriginFinder.getOriginOfMemberTree(selectedElement);
            PhpClassMemberResolver phpMemberResolver = new PhpClassMemberResolver(selectedElement, origin);
            phpMemberResolver.resolve();
            return phpMemberResolver.getResolvedPhpClass();
        }

        return null;
    }
}
