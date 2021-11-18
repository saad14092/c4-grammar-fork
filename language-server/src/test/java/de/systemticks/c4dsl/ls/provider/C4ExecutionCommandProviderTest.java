package de.systemticks.c4dsl.ls.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;

import com.google.gson.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.systemticks.c4dsl.ls.helper.C4TestHelper;

public class C4ExecutionCommandProviderTest {
    
    @Test
    public void unknownCommand() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

        C4ExecuteCommandResult result = commandProvider.execute("unknown.command", null);

        assertEquals(C4ExecuteCommandResult.UNKNOWN_COMMAND.getResultCode(), result.getResultCode());
    }

    @Test
    public void illegalArgumentsNull() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", null);

        assertEquals(C4ExecuteCommandResult.ILLEGAL_ARGUMENTS.getResultCode(), result.getResultCode());
    }

    @Test
    public void illegalArgumentsOtherType() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", Arrays.asList("A String"));

        assertEquals(C4ExecuteCommandResult.ILLEGAL_ARGUMENTS.getResultCode(), result.getResultCode());
    }

    @Test
    public void illegalArgumentsErrorInJsonKey() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();
        JsonObject obj = new JsonObject();
        obj.addProperty("wrong", "uri");
        obj.addProperty("json", "renderer");
        obj.addProperty("key", "ourDir");

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", Arrays.asList(obj));

        assertEquals(C4ExecuteCommandResult.ILLEGAL_ARGUMENTS.getResultCode(), result.getResultCode());
    }

    @Test
    public void parserExceptionJsonButWrongConent() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();
        JsonObject obj = new JsonObject();
        obj.addProperty("uri", "uri");
        obj.addProperty("renderer", "renderer");
        obj.addProperty("outDir", "ourDir");

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", Arrays.asList(obj));

        assertEquals(C4ExecuteCommandResult.STRUCTURIZR_PARSER_EXCEPTION.getResultCode(), result.getResultCode());
    }

    @Test
    public void generateOk(@TempDir File outDir) throws MalformedURLException {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

        JsonObject obj = new JsonObject();
        obj.addProperty("uri", C4TestHelper.PATH_VALID_MODELS + File.separator + "amazon_web_service.dsl");
        obj.addProperty("renderer", "StructurizrPlantUMLWriter");
        obj.addProperty("outDir", outDir.getAbsolutePath());

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", Arrays.asList(obj));

        assertEquals(C4ExecuteCommandResult.OK.getResultCode(), result.getResultCode());

        assertEquals("AmazonWebServicesDeployment.puml", outDir.list()[0]);
    }

}
