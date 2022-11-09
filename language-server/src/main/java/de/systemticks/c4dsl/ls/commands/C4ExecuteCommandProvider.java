package de.systemticks.c4dsl.ls.commands;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.model.C4DocumentManager;

public class C4ExecuteCommandProvider {

    public static final String EXPORT_FILE_TO_PUML = "c4-server.export.puml";
    public static final String UPDATE_CONFIGURATION = "c4-server.configuration";
    public static final String CALCULATE_TEXT_DECORATIONS = "c4-server.text-decorations";

    private static final Logger logger = LoggerFactory.getLogger(C4ExecuteCommandProvider.class);

    public C4ExecuteCommandResult execute(String command, List<Object> arguments, C4DocumentManager documentManager) {

        switch (command) {
            case EXPORT_FILE_TO_PUML:
                if(arguments == null || arguments.size() != 1) {
                    return C4ExecuteCommandResult.ILLEGAL_ARGUMENTS;
                }
                return new C4ExportPlantUmlHandler().handleRequest(arguments);
            
            case UPDATE_CONFIGURATION:
                logger.info("Update configuration {}", arguments.get(0).toString());
                return C4ExecuteCommandResult.OK;

            case CALCULATE_TEXT_DECORATIONS:
                logger.error("CALCULATE_TEXT_DECORATIONS should not be handled here");
                return C4ExecuteCommandResult.UNKNOWN_FAILURE;

            default:
                logger.error("Unknown command {}", command);
                return C4ExecuteCommandResult.UNKNOWN_COMMAND.setMessage(command);
        }
    }
    
}
