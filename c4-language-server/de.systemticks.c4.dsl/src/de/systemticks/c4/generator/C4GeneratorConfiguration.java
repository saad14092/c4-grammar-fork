package de.systemticks.c4.generator;

import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.BasicPlantUMLWriter;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.io.mermaid.MermaidWriter;
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter;


public enum C4GeneratorConfiguration {

    INSTANCE; 
	 
    private String renderer;

    public enum WriterType {
        PlantUML, Mermaid;
    }
    
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

    public WriterType getWriterType() {
        if (renderer.equals("MermaidWriter")) {
            return WriterType.Mermaid;
        }
        else {
            return WriterType.PlantUML;
        }
    }
    
    public PlantUMLWriter getWriter() {
    	
    	//System.err.println("renderer: "+renderer);
    	
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

    public MermaidWriter getMermaidWriter() {
    	
    	// System.err.println("renderer: "+renderer);
        return new MermaidWriter();    	
    }
}
