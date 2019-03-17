package twig.assertion.navigation;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import com.jetbrains.twig.TwigTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import twig.assertion.util.FindElements;
import twig.assertion.util.Fqn;

import java.util.Optional;

import static com.jetbrains.twig.elements.TwigElementTypes.FUNCTION_CALL;
import static com.jetbrains.twig.elements.TwigElementTypes.METHOD_CALL;

public class PhpClassMemberResolver {
    private final PhpIndex phpIndex;
    private final PsiElement resolvingTarget;
    private final PsiElement originElement;
    private PhpNamedElement currentNavigationTarget;
    private PhpClass prevElementType;
    private String previousElementClassName;

    private PsiElement resolvedPsiElement;
    private PhpClass resolvedPhpClass;

    public PhpClassMemberResolver(PsiElement psiElement, PsiElement originElement) {
        phpIndex = PhpIndex.getInstance(psiElement.getProject());
        this.resolvingTarget = psiElement;
        this.originElement = originElement;
    }

    public void resolve() {
        try {
            initState();
            walk(originElement);
        } catch (Throwable t) {
            handleError();
        }
    }

    private void initState() {
        resolvedPsiElement = null;
        resolvedPhpClass = null;
        currentNavigationTarget = null;
        prevElementType = null;
        previousElementClassName = "";
    }

    private void handleError() {
        resolvedPsiElement = null;
        resolvedPhpClass = null;
    }

    private void walk(PsiElement current) {
        IElementType type = current.getNode().getElementType();

        if (type == METHOD_CALL) {
            currentNavigationTarget = findMethodType(current, prevElementType);
            previousElementClassName = currentNavigationTarget.getDeclaredType().toString();
        } else if (type == FUNCTION_CALL) {
            currentNavigationTarget = findFunctionCallType(current, prevElementType);
            previousElementClassName = ((MethodImpl) currentNavigationTarget).getReturnType().getType().toString();
        } else if (type == TwigTokenTypes.IDENTIFIER) {
            if (prevElementType == null) {
                previousElementClassName = getClassNameFromAssertType(current);
            } else {
                currentNavigationTarget = findFieldType(current, prevElementType);
                previousElementClassName = currentNavigationTarget.getDeclaredType().toString();
            }
        }

        if (reachedResolvingTarget(current, resolvingTarget)) {
            resolvedPsiElement = currentNavigationTarget;
            resolvedPhpClass = getPhpClassFromMember(previousElementClassName);
            return;
        }

        prevElementType = getPhpClassFromMember(previousElementClassName);
        if (prevElementType == null) {
            return;
        }

        walk(current.getNextSibling());
    }

    private boolean reachedResolvingTarget(@NotNull PsiElement current, PsiElement psiElement) {
        if (current.equals(psiElement)) {
            return true;
        }

        current = current.getFirstChild();
        if (current == null) {
            return false;
        }

        do {
            if (current.equals(psiElement)) {
                return true;
            }

        } while ((current = current.getNextSibling()) != null);

        return false;
    }

    @Nullable
    private Method findFunctionCallType(@NotNull PsiElement current, @NotNull PhpClass phpClass) {
        String functionName = current.getFirstChild().getText();

        return phpClass.findOwnMethodByName(functionName);
    }

    @Nullable
    private Field findFieldType(@NotNull PsiElement current, @NotNull PhpClass phpClass) {
        String fieldName = current.getText();

        return phpClass.findOwnFieldByName(fieldName, false);
    }

    @Nullable
    private Method findMethodType(PsiElement current, PhpClass phpClass) {
        if (phpClass == null) {
            String originClassName = getClassNameFromAssertType(current.getFirstChild());
            Optional<PhpClass> optionalOriginClass = phpIndex.getClassesByFQN(originClassName).stream().findFirst();
            if (!optionalOriginClass.isPresent()) {
                return null;
            }
            phpClass = optionalOriginClass.get();
        }
        String methodName = current.getFirstChild().getNextSibling().getNextSibling().getText();

        return phpClass.findOwnMethodByName(methodName);
    }


    private PhpClass getPhpClassFromMember(String className) {
        Optional<PhpClass> optionalClass = phpIndex.getClassesByFQN(className).stream().findFirst();
        return optionalClass.orElse(null);

    }

    @NotNull
    private String getClassNameFromAssertType(@NotNull PsiElement psiElement) {
        String classNameTwigFormatted = FindElements.findAssertTypeName(psiElement.getContainingFile(), psiElement.getText());
        return Fqn.fromTwigString(classNameTwigFormatted);
    }

    PsiElement getResolvedPsiElement() {
        return resolvedPsiElement;
    }

    boolean hasResolvedPsiElement() {
        return resolvedPsiElement != null;
    }

    public PhpClass getResolvedPhpClass() {
        return resolvedPhpClass;
    }
}


