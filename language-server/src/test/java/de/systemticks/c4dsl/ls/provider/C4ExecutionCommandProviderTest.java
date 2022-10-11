package de.systemticks.c4dsl.ls.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;

import com.google.gson.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import de.systemticks.c4dsl.ls.commands.C4ExecuteCommandProvider;
import de.systemticks.c4dsl.ls.commands.C4ExecuteCommandResult;
import de.systemticks.c4dsl.ls.helper.C4TestHelper;
import de.systemticks.c4dsl.ls.model.C4DocumentManager;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;

public class C4ExecutionCommandProviderTest {
    
    @Test
    public void unknownCommand() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

        C4ExecuteCommandResult result = commandProvider.execute("unknown.command", null, null);

        assertEquals(C4ExecuteCommandResult.UNKNOWN_COMMAND.getResultCode(), result.getResultCode());
    }

    @Test
    public void illegalArgumentsNull() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", null, null);

        assertEquals(C4ExecuteCommandResult.ILLEGAL_ARGUMENTS.getResultCode(), result.getResultCode());
    }

    @Test
    public void illegalArgumentsOtherType() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", Arrays.asList("A String"), null);

        assertEquals(C4ExecuteCommandResult.ILLEGAL_ARGUMENTS.getResultCode(), result.getResultCode());
    }

    @Test
    public void illegalArgumentsErrorInJsonKey() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();
        JsonObject obj = new JsonObject();
        obj.addProperty("wrong", "uri");
        obj.addProperty("json", "renderer");
        obj.addProperty("key", "ourDir");

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", Arrays.asList(obj), null);

        assertEquals(C4ExecuteCommandResult.ILLEGAL_ARGUMENTS.getResultCode(), result.getResultCode());
    }

    @Test
    public void parserExceptionJsonButWrongConent() {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();
        JsonObject obj = new JsonObject();
        obj.addProperty("uri", "uri");
        obj.addProperty("renderer", "renderer");
        obj.addProperty("outDir", "ourDir");

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", Arrays.asList(obj), null);

        assertEquals(C4ExecuteCommandResult.STRUCTURIZR_PARSER_EXCEPTION.getResultCode(), result.getResultCode());
    }

    @Test
    public void generateOk(@TempDir File outDir) throws MalformedURLException {

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

        JsonObject obj = new JsonObject();
        obj.addProperty("uri", C4TestHelper.PATH_VALID_MODELS + File.separator + "amazon_web_service.dsl");
        obj.addProperty("renderer", "StructurizrPlantUMLWriter");
        obj.addProperty("outDir", outDir.getAbsolutePath());

        C4ExecuteCommandResult result = commandProvider.execute("c4-server.export.puml", Arrays.asList(obj), null);

        assertEquals(C4ExecuteCommandResult.OK.getResultCode(), result.getResultCode());

        assertEquals("AmazonWebServicesDeployment.puml", outDir.list()[0]);
    }

    @Test
    public void textDecorationsCalled() throws URISyntaxException {

        C4DocumentManager documentManager = mock(C4DocumentManager.class);
        C4DocumentModel model = mock(C4DocumentModel.class);
        when(documentManager.getDocument(any())).thenReturn(model);

        C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

        JsonObject obj = new JsonObject();
        obj.addProperty("uri", C4TestHelper.PATH_VALID_MODELS + File.separator + "amazon_web_service.dsl");
        
        C4ExecuteCommandResult result = commandProvider.execute("c4-server.text-decorations", Arrays.asList(obj), documentManager);

        assertEquals(C4ExecuteCommandResult.TEXT_DECORATIONS.getResultCode(), result.getResultCode());

    }

}
