package twig.assertion.tests.util;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;

import java.nio.file.Paths;

public class PsiElementFromFixtureFileLoader {

    private final CodeInsightTestFixture fixture;
    private final String templateFixtureBasePath;
    private final LanguageFileType languageFileType;

    public PsiElementFromFixtureFileLoader(CodeInsightTestFixture fixture, String templateFixtureBasePath, LanguageFileType languageFileType) {
        this.fixture = fixture;
        this.templateFixtureBasePath = templateFixtureBasePath;
        this.languageFileType = languageFileType;
    }

    public PsiElement resolveAtCaret(String fileName) {
        String fullFilePath = Paths.get(templateFixtureBasePath, fileName).toString();
        fixture.configureByText(languageFileType, FixtureFileReader.readFile(fullFilePath));
        return fixture.getFile().findElementAt(fixture.getCaretOffset());
    }


}
