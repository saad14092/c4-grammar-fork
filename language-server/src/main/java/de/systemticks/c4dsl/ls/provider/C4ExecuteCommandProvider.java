package de.systemticks.c4dsl.ls.provider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import com.structurizr.io.plantuml.BasicPlantUMLWriter;
import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter;
import com.structurizr.view.View;

import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.model.C4DocumentManager;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4ExecuteCommandProvider {

    public static final String EXPORT_FILE_TO_PUML = "c4-server.export.puml";
    public static final String UPDATE_CONFIGURATION = "c4-server.configuration";
    public static final String CALCULATE_TEXT_DECORATIONS = "c4-server.text-decorations";
    private static final String PLANTUML_FILE_EXT = ".puml";
	private Gson gson = new Gson();

    private static final Logger logger = LoggerFactory.getLogger(C4ExecuteCommandProvider.class);

    public C4ExecuteCommandResult execute(String command, List<Object> arguments, C4DocumentManager documentManager) {

        switch (command) {
            case EXPORT_FILE_TO_PUML:

                if(arguments == null) {
                    return C4ExecuteCommandResult.ILLEGAL_ARGUMENTS;
                }
                else if(arguments.size() != 1) {
                    logger.error("Command {} does not contain any options", command);
                }
                else {
                    try {
                        JsonObject options = (JsonObject) arguments.get(0);
                        logger.info("Execute Command {} with options {}",command,options);
                        String uri = options.get("uri").getAsString();
                        String renderer = options.get("renderer").getAsString();
                        String exportDir = options.get("outDir").getAsString();
                        return exportFileToPuml(uri, renderer, exportDir);    
                    }
                    catch(ClassCastException | NullPointerException e) {
                        logger.error("execute {}", e.getMessage());
                        return C4ExecuteCommandResult.ILLEGAL_ARGUMENTS;
                    }
                }
                break;
            
            case UPDATE_CONFIGURATION:
                logger.info("Update configuration {}", arguments.get(0).toString());
                return C4ExecuteCommandResult.OK;

            case CALCULATE_TEXT_DECORATIONS:
                JsonObject options = (JsonObject) arguments.get(0);
                logger.info("Execute Command {} with options {}",command,options);
                logger.info("Calculate Text Decorations");
                C4TextDecoratorProvider decoratorProvider = new C4TextDecoratorProvider();
                String uri = options.get("uri").getAsString();
                List<Range> decorations = new ArrayList<>();
                try {
                    TextDocumentIdentifier documentId = new TextDocumentIdentifier(new File(uri).toURI().toURL().toString());
                    C4DocumentModel model = documentManager.getDocument(documentId);
                    decorations = decoratorProvider.calculateDecorationsForModelNames(model);
                } catch (URISyntaxException | MalformedURLException e) {
                    logger.error(e.getMessage());
                    return C4ExecuteCommandResult.UNKNOWN_FAILURE;
                }
                toJson(decorations);
                return C4ExecuteCommandResult.TEXT_DECORATIONS.setResultData(toJson(decorations));

            default:
            logger.error("Unknown command {}", command);
            return C4ExecuteCommandResult.UNKNOWN_COMMAND.setMessage(command);
        }

        return C4ExecuteCommandResult.UNKNOWN_FAILURE;

    }
    
    private C4ExecuteCommandResult exportFileToPuml(String path, String renderer, String outDir) {

        File dslFile = new File(path);
        StructurizrDslParser parser = new StructurizrDslParser();
        PlantUMLWriter writer = createWriter(renderer);

        try {
            parser.parse(dslFile);

            for(View view: parser.getWorkspace().getViews().getViews()) {
                String puml = writer.toString(view);
                File out = new File(outDir+File.separator+view.getKey()+PLANTUML_FILE_EXT);
                logger.info("exportFileToPuml to File {}", out.getAbsolutePath());
                try {
                    C4Utils.writeContentToFile(out, puml);
                } 
                catch (IOException e) {
                    logger.error("exportFileToPuml {}", e.getMessage());
                    return C4ExecuteCommandResult.IO_EXCEPTION.setMessage(e.getMessage());
                }                
            }

            return C4ExecuteCommandResult.OK.setMessage("PlantUML files successfully exported to "+outDir);

        } catch (StructurizrDslParserException e) {
            logger.error("exportFileToPuml {}", e.getMessage());
            return C4ExecuteCommandResult.STRUCTURIZR_PARSER_EXCEPTION.setMessage(e.getMessage());
        }
    }

    private PlantUMLWriter createWriter(String writer) {

        if(writer.equals("StructurizrPlantUMLWriter")) {
            return new StructurizrPlantUMLWriter();
        }
        else if(writer.equals("C4PlantUMLWriter")) {
            return new C4PlantUMLWriter();
        }
        else if(writer.equals("BasicPlantUMLWriter")) {
            return new BasicPlantUMLWriter();
        }
        else {
            return new StructurizrPlantUMLWriter();
        }
    } 

    private JsonElement toJson(List<Range> ranges) {
        return gson.toJsonTree(ranges);
    }
}
