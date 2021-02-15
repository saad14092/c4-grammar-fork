package de.systemticks.c4.generator;

import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter;

public enum C4GeneratorConfiguration {

    INSTANCE; 
	 
    private String renderer;
 
    private C4GeneratorConfiguration() {
        this.renderer = "StructurizrPlantUMLWriter";
    }
 
    public C4GeneratorConfiguration getInstance() {
        return INSTANCE;
    }	
	
    public void setRenderer(String _renderer) {
    	renderer = _renderer;
    	//System.err.println("setRenderer: "+renderer);
    }
    
    public PlantUMLWriter getWriter() {
    	
    	//System.err.println("renderer: "+renderer);
    	
    	if(renderer.equals("C4PlantUMLWriter")) {
    		return new C4PlantUMLWriter();
    	}
    	
    	else {
    		return new StructurizrPlantUMLWriter();
    	}
    	
    }
}
