package de.systemticks.c4dsl.ls.provider;

import java.util.List;

import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.systemticks.c4dsl.ls.provider.C4TextDecoratorProvider.DecoratorRange;
import static org.assertj.core.api.Assertions.assertThat;

public class C4TextDecoratorProviderTest {
    
    private static C4TextDecoratorProvider decoratorProvider;

    @BeforeEach
    public void setup() {
        decoratorProvider = new C4TextDecoratorProvider();
    }

    @Test
    void componentWithoutIdentifier() {
        String line = "component myname mydescription mytechnology mytags";
        List<DecoratorRange> decorations = decoratorProvider.decorationsForComponent(line, 0);
        assertThat(decorations).hasSize(4);
        assertThat(decorations.stream().map(DecoratorRange::getType)).containsExactly("name: ", "description: ", "technology: ", "tags: ");
        assertThat(decorations.stream().map(dr -> dr.getRange().getStart().getLine())).allMatch(l -> l == 0);
        assertThat(decorations.get(0).getRange().getStart()).isEqualTo(new Position(0, 10));
        assertThat(decorations.get(1).getRange().getStart()).isEqualTo(new Position(0, 17));
        assertThat(decorations.get(2).getRange().getStart()).isEqualTo(new Position(0, 31));
        assertThat(decorations.get(3).getRange().getStart()).isEqualTo(new Position(0, 44));
    }

    @Test
    void componentWithIdentifier() {
        String line = "identifier = component myname mydescription mytechnology mytags";
        List<DecoratorRange> decorations = decoratorProvider.decorationsForComponent(line, 0);
        assertThat(decorations).hasSize(4);
        assertThat(decorations.stream().map(DecoratorRange::getType)).containsExactly("name: ", "description: ", "technology: ", "tags: ");
        assertThat(decorations.stream().map(dr -> dr.getRange().getStart().getLine())).allMatch(l -> l == 0);
    }

    @Test
    void componentWithIdentifierAndBracket() {
        String line = "identifier = component myname mydescription mytechnology mytags {";
        List<DecoratorRange> decorations = decoratorProvider.decorationsForComponent(line, 0);
        assertThat(decorations).hasSize(4);
        assertThat(decorations.stream().map(DecoratorRange::getType)).containsExactly("name: ", "description: ", "technology: ", "tags: ");
        assertThat(decorations.stream().map(dr -> dr.getRange().getStart().getLine())).allMatch(l -> l == 0);
    }

    @Test
    void componentOnlyName() {
        String line = "identifier = component myname";
        List<DecoratorRange> decorations = decoratorProvider.decorationsForComponent(line, 0);
        assertThat(decorations).hasSize(1);
        assertThat(decorations.stream().map(DecoratorRange::getType)).containsExactly("name: ");
        assertThat(decorations.stream().map(dr -> dr.getRange().getStart().getLine())).allMatch(l -> l == 0);
    }

    @Test
    void personWithQuotedDescription() {
        String line = "user = person myname \"mydescription\" \"tag1 tag2\"";
        List<DecoratorRange> decorations = decoratorProvider.decorationsForPerson(line, 0);
        assertThat(decorations).hasSize(3);
        assertThat(decorations.stream().map(DecoratorRange::getType)).containsExactly("name: ", "description: ", "tags: ");
    }

    @Test
    void softwareSystemDecorations() {
        String line = "identifier = softwareSystem myname \"mydescription\"  \"mytags\" {";
        List<DecoratorRange> decorations = decoratorProvider.decorationsForSoftwareSystem(line, 0);
        assertThat(decorations).hasSize(3);
        assertThat(decorations.stream().map(DecoratorRange::getType)).containsExactly("name: ", "description: ", "tags: ");
    }

}
