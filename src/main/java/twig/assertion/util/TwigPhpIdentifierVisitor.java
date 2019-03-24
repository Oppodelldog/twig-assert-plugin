package twig.assertion.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TwigPhpIdentifierVisitor extends PsiRecursiveElementVisitor {
    private final IdentifierHandler handler;

    private final PhpIndex phpIndex;
    private final PsiElement objectVariableElement;
    private PhpClass statePrevPhpType;
    private boolean stateExpectDot;

    public TwigPhpIdentifierVisitor(PhpClass startElement, PhpIndex phpIndex, IdentifierHandler handler, PsiElement objectVariableElement) {
        this.handler = handler;
        this.phpIndex = phpIndex;

        this.statePrevPhpType = startElement;
        this.objectVariableElement = objectVariableElement;
        this.stateExpectDot = false;
    }

    @Override
    public void visitElement(PsiElement element) {
        try {
            //noinspection StatementWithEmptyBody
            if (element.equals(objectVariableElement)) {
                //IGNORE object variable element, it cannot be resolved
            } else if (element.getNode().getElementType() == TwigTokenTypes.IDENTIFIER) {
                expectDot();
                element = handleIdentifier(element);
                if (element == null) return;
            } else if (element.getNode().getElementType() == TwigTokenTypes.DOT) {
                notExpectDot();
            } else if (element.getNode().getElementType() == TwigElementTypes.METHOD_CALL) {
                visitElement(element.getFirstChild());
            } else if (element.getNode().getElementType() == TwigElementTypes.FUNCTION_CALL) {
                visitElement(element.getFirstChild());
            } else
                //noinspection StatementWithEmptyBody
                if (element.getNode().getElementType() == TwigTokenTypes.LBRACE) {
                } else if (element.getNode().getElementType() == TwigTokenTypes.RBRACE) {
                    stateExpectDot = true;
                    //throw new TypeChainException("unexpected element type: " + element.getNode().getElementType());
                }

            PsiElement nextSibling = element.getNextSibling();
            if (nextSibling != null) {
                visitElement(nextSibling);
            }
        } catch (TypeChainException e) {
            // Happens when resolving twig to php fails.
            // No serious issue, but leads to stop renaming elements.
            // Is caused by simple twig syntax errors, unexpected tokens,
            // missing fields, methods or type declarations in php etc.
        }
    }

    private void expectDot() throws TypeChainException {
        if (stateExpectDot) {
            throw new TypeChainException("expected dot");
        }
    }

    private void notExpectDot() {
        stateExpectDot = false;
    }

    private PsiElement handleIdentifier(@NotNull PsiElement element) throws TypeChainException {
        boolean isElementFunctionName = (element.getNextSibling() != null && element.getNextSibling().getNode().getElementType() == TwigTokenTypes.LBRACE);
        if (isElementFunctionName) {
            element = handler.handleMethod(element, statePrevPhpType);
            if (element == null) return null;

            setPrevPhpTypeByMethod(element.getNode().getText());
            return element;
        }

        element = handler.handleField(element, statePrevPhpType);
        if (element == null) return null;

        setPrevPhpTypeByField(element.getText());
        return element;
    }

    private void setPrevPhpTypeByMethod(String methodName) throws TypeChainException {
        Method method = statePrevPhpType.findOwnMethodByName(methodName);

        if (method == null) {
            throw new TypeChainException(String.format("method not found:'%s' class:'%s'", methodName, statePrevPhpType.getFQN()));
        }

        if (method.getReturnType() == null) {
            throw new TypeChainException(String.format("method return type not found. method:'%s' class:'%s'", methodName, statePrevPhpType.getFQN()));
        }

        setPrevTypeByFQCN(method.getReturnType().getType());
    }

    private void setPrevPhpTypeByField(String fieldName) throws TypeChainException {
        Field field = statePrevPhpType.findOwnFieldByName(fieldName, false);
        if (field == null) {
            throw new TypeChainException(String.format("field not found:'%s' class:'%s'", fieldName, statePrevPhpType.getFQN()));
        }

        setPrevTypeByFQCN(field.getDeclaredType());
    }

    private void setPrevTypeByFQCN(@NotNull PhpType declaredType) throws TypeChainException {
        String fieldTypeFQCN = declaredType.toString();
        Optional<PhpClass> first = phpIndex.getClassesByFQN(fieldTypeFQCN).stream().findFirst();
        if (!first.isPresent())
            throw new TypeChainException(String.format("php class type not found for '%s'", fieldTypeFQCN));

        statePrevPhpType = first.get();
        stateExpectDot = true;
    }

}
