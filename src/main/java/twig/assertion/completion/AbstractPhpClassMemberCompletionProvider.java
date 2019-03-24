package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigElementTypes;
import org.jetbrains.annotations.Nullable;
import twig.assertion.navigation.IdentifierFinder;
import twig.assertion.util.*;

import java.util.Optional;

abstract class AbstractPhpClassMemberCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Nullable
    PhpClass findPhpClassForVariableLeftFromCaret(CompletionParameters parameters) {
        PsiElement currElement = parameters.getPosition().getOriginalElement();
        if (currElement.getNode().getElementType() == TwigTokenTypes.IDENTIFIER && currElement.getPrevSibling().getText().equals(".")) {

            PsiElementAccessor nav = new PsiElementAccessor(currElement);
            PsiElement selectedElement = nav.getPrevious(2).orElse(null);
            if (selectedElement == null) {
                return null;
            }
            PsiElement origin = TwigAccessOriginFinder.getOriginOfMemberTree(selectedElement);

            PhpIndex phpIndex = PhpIndex.getInstance(selectedElement.getProject());
            assert origin != null;
            String classNameTwigFormatted = FindElements.findAssertTypeName(origin.getContainingFile(), origin.getText());
            String className = TwigFqn.fromTwigString(classNameTwigFormatted);
            Optional<PhpClass> optionalClass = phpIndex.getClassesByFQN(className).stream().findFirst();
            if (!optionalClass.isPresent()) {
                return null;
            }
            if (selectedElement.getNode().getElementType() == TwigElementTypes.FUNCTION_CALL) {
                selectedElement = selectedElement.getFirstChild();
            }
            IdentifierFinder catcher = new IdentifierFinder(selectedElement);

            PsiElement objectVariableElement = origin;
            if (origin.getNode().getElementType() == TwigElementTypes.METHOD_CALL) {
                objectVariableElement = origin.getFirstChild();
            }

            if (origin.equals(selectedElement)) {
                return optionalClass.get();
            }

            TwigPhpIdentifierVisitor visitor = new TwigPhpIdentifierVisitor(optionalClass.get(), phpIndex, catcher, objectVariableElement);
            visitor.visitElement(origin);

            if (catcher.hasFoundElement()) {
                PhpNamedElement element = catcher.getFoundElement();
                if (element instanceof PhpClassMember) {
                    return phpIndex.getClassesByFQN(element.getType().toString()).stream().findFirst().orElse(null);
                }
            }
        }

        return null;
    }
}
