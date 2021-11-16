package de.systemticks.c4dsl.ls.helper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.eclipse.lsp4j.TextDocumentIdentifier;

import de.systemticks.c4dsl.ls.model.C4DocumentManager;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;

public class C4TestHelper {
    
    public static List<String> MODELS_TO_TEST = Arrays.asList("amazon_web_service.dsl", "big_bank.dsl", "financial_risk.dsl", "c4-dsl-extension.dsl");

    public static String PATH_INVALID_MODELS = String.join(File.separator, Arrays.asList("src", "test", "java", "resources", "invalid"));

    public static String PATH_VALID_MODELS = String.join(File.separator, Arrays.asList("src", "test", "java", "resources", "valid"));

    public static C4DocumentModel createDocumentFromFile(File file) throws IOException, URISyntaxException {
        C4DocumentManager c4 = new C4DocumentManager();
        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        c4.calcDiagnostics(file, content);
        TextDocumentIdentifier documentId = new TextDocumentIdentifier(file.toURI().toURL().toString());
        return c4.getDocument(documentId);
    }


}
