package de.systemticks.c4dsl.ls.commands;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.systemticks.c4dsl.ls.model.C4DocumentManager;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.provider.C4TextDecoratorProvider;
import de.systemticks.c4dsl.ls.provider.C4TextDecoratorProvider.DecoratorRange;

public class C4TextDecorationHandler implements C4CommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(C4TextDecorationHandler.class);
    private Gson gson;
    private C4DocumentManager documentManager;

    public C4TextDecorationHandler(C4DocumentManager documentManager) {
        gson = new Gson();
        this.documentManager = documentManager;
    }

    @Override
    public C4ExecuteCommandResult handleRequest(List<Object> arguments) {
        logger.info("Calculate Text Decorations");
        JsonObject options = (JsonObject) arguments.get(0);
        C4TextDecoratorProvider decoratorProvider = new C4TextDecoratorProvider();
        String uri = options.get("uri").getAsString();
        List<DecoratorRange> decorations = new ArrayList<>();
        try {
            TextDocumentIdentifier documentId = new TextDocumentIdentifier(new File(uri).toURI().toURL().toString());
            C4DocumentModel model = documentManager.getDocument(documentId);
            decorations = decoratorProvider.calculateDecorations(model);
        } catch (URISyntaxException | MalformedURLException e) {
            logger.error(e.getMessage());
            return C4ExecuteCommandResult.UNKNOWN_FAILURE;
        }
        toJson(decorations);
        return C4ExecuteCommandResult.TEXT_DECORATIONS.setResultData(toJson(decorations));
    }
    
    private JsonElement toJson(List<DecoratorRange> decorations) {
        return gson.toJsonTree(decorations);
    }

}
