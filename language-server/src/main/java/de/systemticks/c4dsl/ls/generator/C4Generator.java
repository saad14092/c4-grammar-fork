package de.systemticks.c4dsl.ls.generator;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.Deflater;

import com.structurizr.Workspace;
import com.structurizr.export.AbstractDiagramExporter;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
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

//	public static void generateEncodedWorkspace(StructurizrDslParser parser, String outDir) throws Exception {
//		final String workspaceJson = WorkspaceUtils.toJson(parser.getWorkspace(), false);
//		final String encodedWorkspace = Base64.getEncoder().encodeToString(workspaceJson.getBytes());
//		generateToFile(new File(outDir+File.separator+"_workspace.enc"), encodedWorkspace);		
//	}

	public static String generateEncodedWorkspace(Workspace workspace) throws Exception {
		return Base64.getEncoder().encodeToString(WorkspaceUtils.toJson(workspace, false).getBytes());
	}

	public static String generateEncodedPlantUml(View view, AbstractDiagramExporter exporter) throws Exception {
		//FIXME optional might be empty
		String puml = createPuml(view, exporter).get();
		return new String(Base64.getUrlEncoder().encode(compress(puml.getBytes())));
//		return Base64.getEncoder().encodeToString(puml.getBytes());
	}

	private static byte[] compress(byte[] source) throws IOException {
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		deflater.setInput(source);
		deflater.finish();
	
		byte[] buffer = new byte[32768];
		int compressedLength = deflater.deflate(buffer);
		byte[] result = new byte[compressedLength];
		System.arraycopy(buffer, 0, result, 0, compressedLength);
		return result;
	}	

    public static Optional<String> createPuml(View view, AbstractDiagramExporter exporter) {

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
