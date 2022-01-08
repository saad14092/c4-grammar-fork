package de.systemticks.c4dsl.ls.provider;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.lsp4j.Color;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.ColorPresentation;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

import de.systemticks.c4dsl.ls.helper.C4TestHelper;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;

public class C4ColorProviderTest {
    
    @Test
    public void calcColorPresentations() {

        C4ColorProvider colorProvider = new C4ColorProvider();
        
        List<ColorPresentation> result = colorProvider.calcColorPresentations(new Color(1.0, 1.0, 1.0, 0.0));
        assertEquals(1, result.size());
        assertEquals("#FFFFFF", result.get(0).getLabel());

    }

    @Test
    public void calcDocumentColors() throws IOException, URISyntaxException {

        C4ColorProvider colorProvider = new C4ColorProvider();

        C4DocumentModel document = C4TestHelper.createDocumentFromFile( new File(C4TestHelper.PATH_VALID_MODELS + File.separator + "amazon_web_service.dsl"));

        assertDoesNotThrow( () -> {
            List<ColorInformation> result = colorProvider.calcDocumentColors(document);
            assertEquals(1, result.size());

            assertEquals(1.0, result.get(0).getColor().getRed());
            assertEquals(1.0, result.get(0).getColor().getGreen());
            assertEquals(1.0, result.get(0).getColor().getBlue());

            assertEquals(new Range(new Position(44, 27), new Position(44, 34)), result.get(0).getRange());
        });

    }

}
