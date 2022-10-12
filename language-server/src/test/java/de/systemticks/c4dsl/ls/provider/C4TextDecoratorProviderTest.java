package de.systemticks.c4dsl.ls.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.structurizr.model.Container;
import com.structurizr.model.Element;

import de.systemticks.c4dsl.ls.helper.C4TestHelper;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.provider.C4TextDecoratorProvider.DecoratorRange;
import de.systemticks.c4dsl.ls.provider.C4TextDecoratorProvider.PositionAndLength;

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
        List<DecoratorRange> ranges = decoratorProvider.calculateDecorations(model);
        assertTrue(!ranges.isEmpty());
        System.err.println("Range: "+ranges.get(0));
    }

    @Test
    public void oneCustomTag() {
        Element element = mock(Element.class);
        when(element.getTags()).thenReturn("Default1, Default2, Custom1");
        when(element.getDefaultTags()).thenReturn(new HashSet<>(Arrays.asList("Default1", "Default2")));
        Optional<String> firstCustom = decoratorProvider.getFirstCustomTag(element);
        assertTrue(firstCustom.isPresent());
        assertEquals("Custom1", firstCustom.get());
    }

    @Test
    public void twoCustomTags() {
        Element element = mock(Element.class);
        when(element.getTags()).thenReturn("Default1, Default2, Custom1, Custom2");
        when(element.getDefaultTags()).thenReturn(new HashSet<>(Arrays.asList("Default1", "Default2")));
        Optional<String> firstCustom = decoratorProvider.getFirstCustomTag(element);
        assertTrue(firstCustom.isPresent());
        assertEquals("Custom1", firstCustom.get());
    }

    @Test
    public void noCustomTags() {
        Element element = mock(Element.class);
        when(element.getTags()).thenReturn("Default1, Default2");
        when(element.getDefaultTags()).thenReturn(new HashSet<>(Arrays.asList("Default1", "Default2")));
        Optional<String> firstCustom = decoratorProvider.getFirstCustomTag(element);
        assertFalse(firstCustom.isPresent());
    }

    @Test
    public void positionAndLengthContainerName() {
        final String LINE = "id = container \"Container Name\" \"Fancy Description\" \"Cool Technology\" {";
        Container container = mock(Container.class);
        when(container.getName()).thenReturn("Container Name");
        PositionAndLength posAndLength = decoratorProvider.calculatePositionAndLengthObject(LINE, container.getName(), 0);
        assertEquals(15, posAndLength.getStartPos());

        when(container.getTechnology()).thenReturn("Cool Technology");
        posAndLength = decoratorProvider.calculatePositionAndLengthObject(LINE, ((Container)container).getTechnology(), 40);
        assertEquals(52, posAndLength.getStartPos());
    }

}
