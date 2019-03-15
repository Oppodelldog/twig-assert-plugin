package twig.assertion.tests.util;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import junit.framework.TestCase;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;

public class PsiElementFromFixtureFile {

    private final CodeInsightTestFixture fixture;
    private final String templateFixtureBasePath;
    private final LanguageFileType languageFileType;

    public PsiElementFromFixtureFile(CodeInsightTestFixture fixture, String templateFixtureBasePath, LanguageFileType languageFileType) {

        this.fixture = fixture;
        this.templateFixtureBasePath = templateFixtureBasePath;
        this.languageFileType = languageFileType;
    }

    public PsiElement resolveAtCaret(String fileName) {
        fixture.configureByText(languageFileType, readFile(fileName));
        return fixture.getFile().findElementAt(fixture.getCaretOffset());
    }

    private String readFile(String filePath) {
        String fullFilePath = Paths.get(templateFixtureBasePath, filePath).toString();
        try {
            StringBuilder sb = new StringBuilder();
            Scanner scan = new Scanner(new File(fullFilePath));
            while (scan.hasNext()) {
                sb.append(scan.nextLine()).append('\n');
            }
            return sb.toString();
        } catch (Throwable t) {
            TestCase.fail(String.format("failed to read file from %s:  %s", fullFilePath, t.getMessage()));
        }
        return "";
    }
}
