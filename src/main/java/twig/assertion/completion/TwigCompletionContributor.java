package twig.assertion.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.twig.elements.TwigCompositeElement;


class TwigCompletionContributor extends CompletionContributor {
    TwigCompletionContributor() {
        this.extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(TwigCompositeElement.class), new PhpFQCNCompletionProvider());
        this.extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(TwigCompositeElement.class), new PhpMethodCompletionProvider());
        this.extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(TwigCompositeElement.class), new PhpFieldCompletionProvider());
        this.extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(TwigCompositeElement.class), new TwigAssertCompletionProvider());
    }
}
