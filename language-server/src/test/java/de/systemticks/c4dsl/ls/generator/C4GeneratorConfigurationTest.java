package de.systemticks.c4dsl.ls.generator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.structurizr.io.plantuml.BasicPlantUMLWriter;
import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter;

import org.junit.jupiter.api.Test;

public class C4GeneratorConfigurationTest {
    
    @Test
    public void getWriter() {

        assertNotNull(C4GeneratorConfiguration.INSTANCE.getWriter());
        assertTrue(C4GeneratorConfiguration.INSTANCE.getWriter() instanceof StructurizrPlantUMLWriter);

        C4GeneratorConfiguration.INSTANCE.setRenderer("C4PlantUMLWriter");
        assertTrue(C4GeneratorConfiguration.INSTANCE.getWriter() instanceof C4PlantUMLWriter);

        C4GeneratorConfiguration.INSTANCE.setRenderer("BasicPlantUMLWriter");
        assertTrue(C4GeneratorConfiguration.INSTANCE.getWriter() instanceof BasicPlantUMLWriter);

        C4GeneratorConfiguration.INSTANCE.setRenderer("AnyOther");
        assertTrue(C4GeneratorConfiguration.INSTANCE.getWriter() instanceof StructurizrPlantUMLWriter);

    }    

}
