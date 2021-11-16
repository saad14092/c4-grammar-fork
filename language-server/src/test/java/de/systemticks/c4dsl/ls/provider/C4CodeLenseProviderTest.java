package de.systemticks.c4dsl.ls.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import java.util.Arrays;
import java.util.List;

import org.eclipse.lsp4j.CodeLens;
import org.junit.jupiter.api.Test;

import de.systemticks.c4dsl.ls.helper.C4TestHelper;


public class C4CodeLenseProviderTest {
    
    @Test
    public void noCodeLenses() {

        C4CodeLenseProvider codeLenseProvider = new C4CodeLenseProvider();
        
        C4TestHelper.MODELS_TO_TEST.forEach( model -> {
            File testFile = new File(C4TestHelper.PATH_INVALID_MODELS + File.separator + model);
            try {
                List<CodeLens> codeLenses = codeLenseProvider.calcCodeLenses(C4TestHelper.createDocumentFromFile(testFile));
                assertEquals(0, codeLenses.size());
            } 
            catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }    
        });
    }

    @Test
    public void codeLenses() {

        C4CodeLenseProvider codeLenseProvider = new C4CodeLenseProvider();
        
        File testFile = new File(C4TestHelper.PATH_VALID_MODELS + File.separator + "amazon_web_service.dsl");
        try {
            List<CodeLens> codeLenses = codeLenseProvider.calcCodeLenses(C4TestHelper.createDocumentFromFile(testFile));
            assertEquals(1, codeLenses.size());

            assertEquals("c4.show.diagram", codeLenses.get(0).getCommand().getCommand());
            assertEquals(36, codeLenses.get(0).getRange().getStart().getLine());
            assertEquals(8, codeLenses.get(0).getRange().getStart().getCharacter());
            assertEquals(36, codeLenses.get(0).getRange().getEnd().getLine());
            assertEquals(8, codeLenses.get(0).getRange().getEnd().getCharacter());

            assertNotNull(codeLenses.get(0).getCommand().getArguments().get(0));

            assertEquals("AmazonWebServicesDeployment",codeLenses.get(0).getCommand().getArguments().get(1));

        } 
        catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
