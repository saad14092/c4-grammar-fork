package de.systemticks.c4dsl.ls.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.structurizr.model.SoftwareSystem;

import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.Test;

import de.systemticks.c4dsl.ls.helper.C4TestHelper;

public class C4DocumentModelTest {
    
    @Test
    public void calcDiagnosticsValidModels() throws IOException {

        C4DocumentManager c4 = new C4DocumentManager();
        
        C4TestHelper.MODELS_TO_TEST.forEach( model -> {
            File testFile = new File(C4TestHelper.PATH_VALID_MODELS + File.separator + model);
            String content;
            try {
                content = new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
                List<PublishDiagnosticsParams> errors = c4.calcDiagnostics(testFile, content);
                assertEquals(0, errors.get(0).getDiagnostics().size());
            } 
            catch (IOException e) {
                e.printStackTrace();
            }    
        });

    }

    @Test
    public void calcDiagnosticsInValidModels() throws IOException {

        C4DocumentManager c4 = new C4DocumentManager();
        
        C4TestHelper.MODELS_TO_TEST.forEach( model -> {
            File testFile = new File(C4TestHelper.PATH_INVALID_MODELS + File.separator + model);
            String content;
            try {
                content = new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
                List<PublishDiagnosticsParams> errors = c4.calcDiagnostics(testFile, content);
                assertEquals(1, errors.get(0).getDiagnostics().size());
            } 
            catch (IOException e) {
                e.printStackTrace();
            }    
        });
    }

    @Test
    public void getDocument() throws IOException {

        C4DocumentManager c4 = new C4DocumentManager();
        File testFile = new File(C4TestHelper.PATH_VALID_MODELS + File.separator + "c4-dsl-extension.dsl");
        String content = new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
        c4.calcDiagnostics(testFile, content);

        TextDocumentIdentifier documentId = new TextDocumentIdentifier(testFile.toURI().toURL().toString());
        try {
            C4DocumentModel model = c4.getDocument(documentId);

            assertEquals(3, model.getAllViews().size());
            assertTrue(model.getViewAtLineNumber(74).isPresent());
            assertTrue(model.getViewAtLineNumber(80).isPresent());
            assertTrue(model.getViewAtLineNumber(86).isPresent());

            assertFalse(model.getViewAtLineNumber(90).isPresent());

            assertEquals(2, model.getAllElements().stream().filter(ele -> (ele.getValue().getObject() instanceof SoftwareSystem)).collect(Collectors.toList()).size());

            assertEquals(17, model.getAllRelationships().size());


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
