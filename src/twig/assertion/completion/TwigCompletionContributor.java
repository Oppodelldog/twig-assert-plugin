package twig.assertion.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.twig.elements.TwigCompositeElement;


class TwigCompletionContributor extends CompletionContributor {
    TwigCompletionContributor() {
        this.extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(TwigCompositeElement.class), new TwigKeywordCompletionContributor());
    }
}
