package de.systemticks.c4dsl.ls.provider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.model.C4TokensConfig;
import de.systemticks.c4dsl.ls.model.C4TokensLoader;

import static org.assertj.core.api.Assertions.assertThat;

public class C4CompletionProviderTest {
    
    private C4TokensLoader configLoader;
    private C4CompletionProvider completionProvider;
    private C4DocumentModel model;

    @BeforeEach
    public void setup() {
        model = mock(C4DocumentModel.class);
        configLoader = mock(C4TokensLoader.class);
        C4TokensConfig config = new C4TokensConfig();
        config.addScope("SoftwareSystemDslContext", Arrays.asList("keyword1", "keyword2", "keyword3"), true);
        config.addScope("ContainerDslContext", Arrays.asList("keyword4", "keyword5"), true);
        when(configLoader.readConfiguration()).thenReturn(config);
        completionProvider = new C4CompletionProvider(configLoader);
    }

    @Test
    public void completionItemsForSoftwareSystemContext() {
        final int lineNumber = 3;
        when(model.getLineAt(lineNumber)).thenReturn("");
        when(model.getSurroundingScope(lineNumber)).thenReturn("SoftwareSystemDslContext");
        List<CompletionItem> result = completionProvider.calcCompletions(model, new Position(lineNumber, 3));

        assertThat(result.stream().map( CompletionItem::getLabel)).containsExactly("keyword1", "keyword2", "keyword3");
        assertThat(result.stream().map( CompletionItem::getKind)).allMatch((kind) -> kind.equals(CompletionItemKind.Keyword));
    }

    @Test
    public void completionItemsForContainerContext() {
        final int lineNumber = 3;
        when(model.getLineAt(lineNumber)).thenReturn("");
        when(model.getSurroundingScope(lineNumber)).thenReturn("ContainerDslContext");
        List<CompletionItem> result = completionProvider.calcCompletions(model, new Position(lineNumber, 3));

        assertThat(result.stream().map( CompletionItem::getLabel)).containsExactly("keyword4", "keyword5");
        assertThat(result.stream().map( CompletionItem::getKind)).allMatch((kind) -> kind.equals(CompletionItemKind.Keyword));
    }

    @Test
    public void noCompletionItemsWhenUnknownScope() {
        final int lineNumber = 3;
        when(model.getSurroundingScope(lineNumber)).thenReturn(C4DocumentModel.NO_SCOPE);
        List<CompletionItem> result = completionProvider.calcCompletions(model, new Position(lineNumber, 3));

        assertThat(result).isEmpty();
    }

}

