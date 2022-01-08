package de.systemticks.c4dsl.ls.generator;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.Deflater;

import com.structurizr.Workspace;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.util.WorkspaceUtils;
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

	public static String generateEncodedPlantUml(View view, PlantUMLWriter writer) throws Exception {
		String puml = writer.toString(view);
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
