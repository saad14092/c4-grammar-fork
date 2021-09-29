package de.systemticks.c4dsl.ls.provider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.JsonPrimitive;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import com.structurizr.io.plantuml.BasicPlantUMLWriter;
import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4ExecuteCommandProvider {

    public static final String EXPORT_FILE_TO_PUML = "c4-server.export.puml";
    private static final String PLANTUML_FILE_EXT = ".puml";

    private static final Logger logger = LoggerFactory.getLogger(C4ExecuteCommandProvider.class);

    public void execute(String command, List<Object> arguments) {

        switch (command) {
            case EXPORT_FILE_TO_PUML:
                if(arguments.size() != 3) {
                    logger.error("Command does not contain an File URI");
                }
                else {
                    JsonPrimitive uri = (JsonPrimitive)arguments.get(0);
                    JsonPrimitive renderer = (JsonPrimitive)arguments.get(1);
                    JsonPrimitive exportDir = (JsonPrimitive)arguments.get(2);
                    exportFileToPuml(uri.getAsString(), renderer.getAsString(), exportDir.getAsString());
                }
                break;
        
            default:
            logger.error("Unknown command {}", command);
            break;
        }

    }
    
    private void exportFileToPuml(String path, String renderer, String outDir) {

        //TODO provide a result code as return value
        logger.info("exportFileToPuml {}", path);

        File dslFile = new File(path);
        StructurizrDslParser parser = new StructurizrDslParser();
        PlantUMLWriter writer = createWriter(renderer);

        try {
            parser.parse(dslFile);

            parser.getWorkspace().getViews().getViews().forEach( view -> {
                String puml = writer.toString(view);
                File out = new File(outDir+File.separator+view.getKey()+PLANTUML_FILE_EXT);
                logger.info("exportFileToPuml to File {}", out.getAbsolutePath());
                try {
                    C4Utils.writeContentToFile(out, puml);
                } catch (IOException e) {
                    logger.error("exportFileToPuml {}", e.getMessage());
                }
            });

        } catch (StructurizrDslParserException e) {
            logger.error("exportFileToPuml {}", e.getMessage());
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
}