package de.systemticks.c4dsl.ls.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.ColorPresentation;
import org.eclipse.lsp4j.ColorPresentationParams;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentColorParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.systemticks.c4dsl.ls.model.C4DocumentManager;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.model.C4TokensLoader;
import de.systemticks.c4dsl.ls.provider.C4CodeLenseProvider;
import de.systemticks.c4dsl.ls.provider.C4ColorProvider;
import de.systemticks.c4dsl.ls.provider.C4CompletionProvider;
import de.systemticks.c4dsl.ls.provider.C4DefinitionProvider;
import de.systemticks.c4dsl.ls.provider.C4SemanticTokenProvider;
import de.systemticks.c4dsl.ls.provider.C4TextDecoratorProvider;
import de.systemticks.c4dsl.ls.provider.C4TextDecoratorProvider.DecoratorRange;

public class C4TextDocumentService implements TextDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(C4TextDocumentService.class);

	private C4LanguageServer ls;

	private C4DocumentManager documentManager = new C4DocumentManager();

	private C4CodeLenseProvider codeLenseProvider = new C4CodeLenseProvider();
	private C4ColorProvider colorProvider = new C4ColorProvider();
	private C4DefinitionProvider definitionProvider = new C4DefinitionProvider();
	private C4SemanticTokenProvider semanticTokenProvider = new C4SemanticTokenProvider();
	private C4TextDecoratorProvider decoratorProvider = new C4TextDecoratorProvider();
	private C4CompletionProvider completionProvider = new C4CompletionProvider(new C4TokensLoader());

	ReadWriteLock lock = new ReentrantReadWriteLock();
	private int changeCount = 0;

	private Gson gson = new Gson();

	public C4TextDocumentService(C4LanguageServer c4LanguageServer) {
		this.ls = c4LanguageServer;
	}

	public C4DocumentManager getDocumentManager() {
		return documentManager;
	}

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {

		return CompletableFuture.supplyAsync( () -> {

			C4DocumentModel model = getDocument(params.getTextDocument());
			if(model != null) {
				return Either.forLeft(completionProvider.calcCompletions(model, params.getPosition()));
			}

			return Either.forLeft(Collections.emptyList());
		});

	}

	@Override
	public CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params) {

		logger.info("documentColor");

		return CompletableFuture.supplyAsync( () -> {
			C4DocumentModel model = getDocument(params.getTextDocument());
			if(model != null && model.isValid()) {
				return colorProvider.calcDocumentColors(model);
			}
			else {
				return Collections.emptyList();
			}
		});
	}


	@Override
	public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
			DefinitionParams params) {

		logger.info("definition");

		return CompletableFuture.supplyAsync( () -> {
			C4DocumentModel model = getDocument(params.getTextDocument());
			if(model != null && model.isValid()) {
				return definitionProvider.calcDefinitions(model, params);
			}
			else {
				return Either.forLeft(Collections.emptyList());
			}
		});
	
	}

	@Override
	public CompletableFuture<List<ColorPresentation>> colorPresentation(ColorPresentationParams params) {

		logger.info("colorPresentation");

		return CompletableFuture.supplyAsync( () -> {
			C4DocumentModel model = getDocument(params.getTextDocument());
			if(model != null && model.isValid()) {
				return colorProvider.calcColorPresentations(params.getColor());
			}
			else {
				return Collections.emptyList();
			}
		});
	}



	@Override
	public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {

		logger.info("semanticTokensFull");

		return CompletableFuture.supplyAsync( () -> {
			
			C4DocumentModel model = getDocument(params.getTextDocument());
			if(model != null && model.isValid()) {
				List<Integer> tokens = semanticTokenProvider.calculateTokens(model);
				return new SemanticTokens(tokens);
			}
			else {
				return new SemanticTokens(new ArrayList<Integer>());
			}
		});
	}

	@Override
	public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {

		logger.info("codeLens");

		return CompletableFuture.supplyAsync( () -> {
	
			C4DocumentModel model = getDocument(params.getTextDocument());
			if(model != null && model.isValid()) {
				return codeLenseProvider.calcCodeLenses(model, this.ls.getRenderer());
			}
			else {
				return Collections.emptyList();
			}
		});
	}

	public JsonElement textDecorations(JsonObject options) {

		logger.info("textDecorations");

		String uri = options.get("uri").getAsString();
		TextDocumentIdentifier documentId;
		try {
			documentId = new TextDocumentIdentifier(new File(uri).toURI().toURL().toString());
			C4DocumentModel model = getDocument(documentId);
			if(model != null && model.isValid()) {
				return toJson(decoratorProvider.calculateDecorations(model));
			} 
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}		
		logger.error("textDecorations == null");
		return null;
	}

	private JsonElement toJson(List<DecoratorRange> decorations) {
        return gson.toJsonTree(decorations);
    }

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {

		String uri = params.getTextDocument().getUri();
		logger.info("didOpen " + uri);
		
		CompletableFuture.runAsync( () -> {
			getDiagnostics(uri, params.getTextDocument().getText()).forEach( d -> {
				ls.getClient().publishDiagnostics(d);
			});
		});	
	
	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		
		String uri = params.getTextDocument().getUri();
		logger.info("didChange " + uri);
		
		CompletableFuture.runAsync( () -> {
			getDiagnostics(uri, params.getContentChanges().get(0).getText()).forEach( d -> {
				ls.getClient().publishDiagnostics(d);
			});
		});	
	
	}

	private List<PublishDiagnosticsParams> getDiagnostics(String uri, String content) {
		
		logger.info("--> getDiagnostics {}", changeCount++);
		lock.writeLock().lock();

		try {
			return documentManager.calcDiagnostics(uriToFile(uri), content);
		} catch (URISyntaxException e) {
			logger.error("getDiagnostics {}", e.getMessage());
			return Collections.emptyList();
		}
		finally {
			lock.writeLock().unlock();
			logger.info("<-- getDiagnostics");
		}
	}

	private C4DocumentModel getDocument(TextDocumentIdentifier documentId) {

		logger.info("--> getDocument");
		lock.readLock().lock();

		try {
			return documentManager.getDocument(documentId);
		} catch (URISyntaxException e) {
			return null;
		}

		finally {
			lock.readLock().unlock();
			logger.info("<-- getDocument");
		}
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		logger.info("didClose " + params.getTextDocument().getUri());
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		logger.info("didSave " + params.getTextDocument().getUri());
	}

	private File uriToFile(String uri) throws URISyntaxException {
		return new File(new URI(uri));
	}

}
