package de.systemticks.c4dsl.ls.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import com.structurizr.io.plantuml.BasicPlantUMLWriter;
import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter;
import com.structurizr.view.View;

import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4ExportPlantUmlHandler implements C4CommandHandler {

    private static final String PLANTUML_FILE_EXT = ".puml";
    private static final Logger logger = LoggerFactory.getLogger(C4ExportPlantUmlHandler.class);
    private Gson gson = new Gson();

    @Override
    public C4ExecuteCommandResult handleRequest(List<Object> arguments) {

        try {
            C4ExportPlantUmlDto request = gson.fromJson((JsonElement)arguments.get(0), C4ExportPlantUmlDto.class);
            if(Objects.isNull(request.getUri()) || Objects.isNull(request.getRenderer()) || Objects.isNull(request.getOutDir())) {
                return C4ExecuteCommandResult.ILLEGAL_ARGUMENTS;
            }
            return exportFileToPuml(request.getUri(), request.getRenderer(), request.getOutDir());
            }
        catch(ClassCastException e) {
            return C4ExecuteCommandResult.ILLEGAL_ARGUMENTS;
        }

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

}
