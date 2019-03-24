package twig.assertion.refactoring;

import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import org.jetbrains.annotations.NotNull;

class RefactoringClassMemberAdapterFactory {

    @NotNull
    static RefactoringClassMemberAdapter create(@NotNull PhpClassMember member) throws Exception {

        if (member.getContainingClass() == null) {
            throw new Exception("containing class must not be null");
        }

        String classFqn = member.getContainingClass().getFQN();
        String originalName = member.getName();

        return new RefactoringClassMemberAdapter(classFqn, originalName);
    }
}
