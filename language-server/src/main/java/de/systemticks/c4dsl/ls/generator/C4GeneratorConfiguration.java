package de.systemticks.c4dsl.ls.generator;

import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.BasicPlantUMLWriter;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter;


public enum C4GeneratorConfiguration {

    INSTANCE; 
	 
    private String renderer;
    
    private C4GeneratorConfiguration() {
        this.renderer = "StructurizrPlantUMLWriter";
    }

    public void setRenderer(String _renderer) {
    	renderer = _renderer;
    }

    public PlantUMLWriter getWriter() {
    	    	
    	if(renderer.equals("C4PlantUMLWriter")) {
    		return new C4PlantUMLWriter();
    	}
    	
        else if(renderer.equals("BasicPlantUMLWriter")) {
    		return new BasicPlantUMLWriter();
        }

    	else {
    		return new StructurizrPlantUMLWriter();
    	}
    	
    }

}
