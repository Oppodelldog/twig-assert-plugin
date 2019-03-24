package twig.assertion.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigElementTypes;
import org.jetbrains.annotations.NotNull;
import twig.assertion.util.FindElements;
import twig.assertion.util.TwigFqn;
import twig.assertion.util.TwigPhpIdentifierVisitor;

import java.util.Optional;

import static twig.assertion.completion.TwigAssertCompletionProvider.ASSERT_TAG_NAME;

class RefactoringClassMemberAdapter extends com.intellij.refactoring.listeners.RefactoringElementAdapter {
    private final String classFqn;
    private final String originalName;
    private String newName;

    RefactoringClassMemberAdapter(String classFqn, String originalName) {
        this.classFqn = classFqn;
        this.originalName = originalName;
    }

    @Override
    protected void elementRenamedOrMoved(@NotNull PsiElement psiElement) {
        if (psiElement instanceof PhpClassMember) {
            final Project project = psiElement.getProject();
            this.newName = ((PhpClassMember) psiElement).getName();
            GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
            PsiSearchHelper psiSearchHelper = PsiSearchHelper.getInstance(project);
            psiSearchHelper.processAllFilesWithWordInLiterals(ASSERT_TAG_NAME, searchScope, psiFile -> {
                if (psiFile.getLanguage() != TwigLanguage.INSTANCE) {
                    return true;
                }
                FindElements.findAllAssertPsiElements(psiFile).
                        forEach(assertElement -> {
                                    String variableName = assertElement.getPrevSibling().getPrevSibling().getPrevSibling().getText();
                                    String fqcn = TwigFqn.fromTwigString(assertElement.getText());
                                    FindElements.findObjectAccessesInFile(assertElement.getContainingFile(), variableName).
                                            forEach(element -> renameAllFieldsInChain(element, fqcn, classFqn));
                                }
                        );
                return true;
            });
        }
    }

    private void renameAllFieldsInChain(PsiElement variableElement, String variableTypeFQCN, String refactoredFQCN) {
        try {
            PhpIndex phpIndex = PhpIndex.getInstance(variableElement.getProject());
            Optional<PhpClass> variableType = phpIndex.getClassesByFQN(variableTypeFQCN).stream().findFirst();
            if (!variableType.isPresent()) {
                throw new Exception("Illegal Object construction. No corresponding PHP Class found to object variable type '" + this.classFqn + "'");
            }
            Optional<PhpClass> refactoredPhpClass = phpIndex.getClassesByFQN(refactoredFQCN).stream().findFirst();
            if (!refactoredPhpClass.isPresent()) {
                throw new Exception("Illegal Object construction. No corresponding PHP Class found to refactored class '" + this.classFqn + "'");
            }
            RenameMemberHandler renameHandler = new RenameMemberHandler(this.newName, this.originalName, refactoredPhpClass.get().getFQN());

            PsiElement parent = variableElement.getParent();
            boolean isFunctionCall = parent.getNode().getElementType() == TwigElementTypes.METHOD_CALL || parent.getNode().getElementType() == TwigElementTypes.FUNCTION_CALL;
            boolean isFunctionResultChained = parent.getNextSibling() != null && parent.getNextSibling().getNode().getElementType() == TwigTokenTypes.DOT;
            if (isFunctionCall && isFunctionResultChained) {
                TwigPhpIdentifierVisitor v = new TwigPhpIdentifierVisitor(variableType.get(), phpIndex, renameHandler, variableElement);
                v.visitElement(parent);
            } else {
                TwigPhpIdentifierVisitor v = new TwigPhpIdentifierVisitor(variableType.get(), phpIndex, renameHandler, variableElement);
                v.visitElement(variableElement);
            }
        } catch (Exception e) {
            //TODO: logging
        }
    }

    @Override
    public void undoElementMovedOrRenamed(@NotNull PsiElement psiElement, @NotNull String s) {
    }


}