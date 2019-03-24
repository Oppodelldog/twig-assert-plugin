package twig.assertion.refactoring;

import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl;
import org.jetbrains.annotations.NotNull;

class RefactoringFQCNAdapterFactory {
    static RefactoringFQCNAdapter create(@NotNull PhpClassImpl field) {

        return new RefactoringFQCNAdapter(field);
    }
}
