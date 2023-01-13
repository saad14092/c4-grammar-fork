package de.systemticks.c4dsl.ls.generator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.structurizr.export.AbstractDiagramExporter;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import com.structurizr.view.ComponentView;
import com.structurizr.view.Configuration;
import com.structurizr.view.ContainerView;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.DynamicView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.SystemLandscapeView;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

import static org.assertj.core.api.Assertions.*;

public class C4GeneratorTest {
    
    @Test
    public void createDiagramExporter() {
        assertAll(
            () -> assertThat(C4Generator.createDiagramExporter("StructurizrPlantUMLWriter")).isInstanceOf(StructurizrPlantUMLExporter.class),
            () -> assertThat(C4Generator.createDiagramExporter("C4PlantUMLWriter")).isInstanceOf(C4PlantUMLExporter.class),
            () -> assertThat(C4Generator.createDiagramExporter("AnyOther")).isInstanceOf(StructurizrPlantUMLExporter.class)
        );
    }

    @ParameterizedTest
    @MethodSource("provideMockedViews")    
    public void createPunlForContainerView(View view) {

        ViewSet viewSet = mock(ViewSet.class);
        Configuration configuration = mock(Configuration.class);
        AbstractDiagramExporter exporter = spy(new MockedExporter());
        when(viewSet.getConfiguration()).thenReturn(configuration);
        when(view.getViewSet()).thenReturn(viewSet);

        view.setDescription("Description");

        Optional<String> puml = C4Generator.createPuml(view, exporter);

        assertAll( 
            () -> assertThat(puml).map(str -> str.startsWith("@startuml")),
            () -> assertThat(puml).map(str -> str.startsWith("@enduml"))    
        );

    }

    private static Stream<Arguments> provideMockedViews() {
        return Stream.of(
            Arguments.of(mock(SystemLandscapeView.class)),
            Arguments.of(mock(SystemContextView.class)),
            Arguments.of(mock(ContainerView.class)),
            Arguments.of(mock(DeploymentView.class)),
            Arguments.of(mock(DynamicView.class)),
            Arguments.of(mock(ComponentView.class))
        );
    }
    
    class MockedExporter extends StructurizrPlantUMLExporter {

        @Override
        public String getViewOrViewSetProperty(View view, String name, String defaultValue) {
            return "mock";
        }
        
    } 

}
