package de.systemticks.c4dsl.ls.generator;

import java.util.Base64;
import java.util.Optional;

import com.structurizr.Workspace;
import com.structurizr.export.AbstractDiagramExporter;
import com.structurizr.export.mermaid.MermaidEncoder;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import com.structurizr.export.plantuml.PlantUMLEncoder;
import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.DynamicView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.SystemLandscapeView;
import com.structurizr.view.View;

public class C4Generator {

	public static String generateEncodedWorkspace(Workspace workspace) throws Exception {
		return Base64.getEncoder().encodeToString(WorkspaceUtils.toJson(workspace, false).getBytes());
	}

	public static String generateEncodedPlantUml(View view, AbstractDiagramExporter exporter) throws Exception {
		//FIXME optional might be empty
		String pumlContent = createDiagramDefinition(view, exporter).get();
        return new PlantUMLEncoder().encode(pumlContent);
	}

	public static String generateEncodedMermaid(View view, AbstractDiagramExporter exporter) throws Exception {
		//FIXME optional might be empty
		String mermaidContent = createDiagramDefinition(view, exporter).get();
        return new MermaidEncoder().encode(mermaidContent);
	}

    public static Optional<String> createDiagramDefinition(View view, AbstractDiagramExporter exporter) {

        if(view instanceof ContainerView) {
            return Optional.ofNullable(exporter.export((ContainerView)view).getDefinition());
        }
        else if(view instanceof ComponentView) {
            return Optional.ofNullable(exporter.export((ComponentView)view).getDefinition());
        }
        else if(view instanceof SystemContextView) {
            return Optional.ofNullable(exporter.export((SystemContextView)view).getDefinition());
        }
        else if(view instanceof SystemLandscapeView) {
            return Optional.ofNullable(exporter.export((SystemLandscapeView)view).getDefinition());
        }
        else if(view instanceof DeploymentView) {
            return Optional.ofNullable(exporter.export((DeploymentView)view).getDefinition());
        }
        else if(view instanceof DynamicView) {
            return Optional.ofNullable(exporter.export((DynamicView)view).getDefinition());
        }
        else {
            return Optional.empty();
        }

    }

    public static AbstractDiagramExporter createDiagramExporter(String writer) {

        if(writer.equals("StructurizrPlantUMLWriter")) {
            return new StructurizrPlantUMLExporter();
        }
        else if(writer.equals("C4PlantUMLWriter")) {
            return new C4PlantUMLExporter();
        }
        else {
            return new StructurizrPlantUMLExporter();
        }
    } 

//	public void generatePlantUML(StructurizrDslParser parser, String outDir) {
//
//		PlantUMLWriter writer = C4GeneratorConfiguration.INSTANCE.getInstance().getWriter();
//		parser.getWorkspace().getViews().getViews().stream().forEach( view -> {
////			generateToFile(new File(outDir+File.separator+view.createFileName+".mmd"), writer.toString(view));						
//		});
//	}
//
//	public void generateMermaid(StructurizrDslParser parser, String outDir) {
//
//		MermaidWriter writer = C4GeneratorConfiguration.INSTANCE.getInstance().getMermaidWriter();
//		parser.getWorkspace().getViews().getViews().stream().forEach( view -> {
////			generateToFile(new File(outDir+File.separator+view.createFileName+".mmd"), writer.toString(view));			
//		});
//	}
//	
//	private static void generateToFile(File out, String content) throws IOException {
//		out.getParentFile().mkdirs();
//		FileWriter fw = new FileWriter(out);
//		fw.write(content);
//		fw.close();
//	}

}
