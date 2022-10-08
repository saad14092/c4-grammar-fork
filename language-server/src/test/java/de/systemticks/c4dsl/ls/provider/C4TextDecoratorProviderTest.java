package de.systemticks.c4dsl.ls.provider;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.systemticks.c4dsl.ls.helper.C4TestHelper;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.provider.C4TextDecoratorProvider.DecoratorRange;

public class C4TextDecoratorProviderTest {
    
    private static C4DocumentModel model;
    private static C4TextDecoratorProvider decoratorProvider;

    @BeforeAll
    public static void initialize() throws IOException, URISyntaxException {
        model = C4TestHelper.createDocumentFromFile(new File(C4TestHelper.PATH_VALID_MODELS + File.separator + "amazon_web_service.dsl"));
        decoratorProvider = new C4TextDecoratorProvider();
    }

    @Test
    public void decorationsForNameCalculated() {
        List<DecoratorRange> ranges = decoratorProvider.calculateDecorationsForModelNames(model);
        assertTrue(!ranges.isEmpty());
        System.err.println("Range: "+ranges.get(0));
    }

}
