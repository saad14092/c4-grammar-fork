package de.systemticks.c4dsl.ls.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.ColorPresentation;
import org.eclipse.lsp4j.ColorPresentationParams;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentColorParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.provider.C4CodeLenseProvider;
import de.systemticks.c4dsl.ls.provider.C4ColorProvider;
import de.systemticks.c4dsl.ls.provider.C4DefinitionProvider;
import de.systemticks.c4dsl.ls.provider.C4HoverProvider;
import de.systemticks.c4dsl.ls.provider.C4SemanticTokenProvider;
import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4TextDocumentService implements TextDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(C4TextDocumentService.class);

	private C4LanguageServer ls;

	private C4CodeLenseProvider codeLenseProvider = new C4CodeLenseProvider();
	private C4HoverProvider hoverProvider = new C4HoverProvider();
	private C4ColorProvider colorProvider = new C4ColorProvider();
	private C4DefinitionProvider definitionProvider = new C4DefinitionProvider();
	private C4SemanticTokenProvider semanticTokenProvider = new C4SemanticTokenProvider();
	
	private Map<String, C4DocumentModel> c4Models = new HashMap<>();
	
	public C4TextDocumentService(C4LanguageServer c4LanguageServer) {
		this.ls = c4LanguageServer;
	}

	

	@Override
	public CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params) {

		C4DocumentModel model = c4Models.get(params.getTextDocument().getUri());
		
		if(model != null) {
			return CompletableFuture.supplyAsync( () -> {
				return colorProvider.calcDocumentColors(model);
			});
		}

		return null;
	}


	@Override
	public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
			DefinitionParams params) {

		C4DocumentModel model = c4Models.get(params.getTextDocument().getUri());

		if(model != null) {
			return CompletableFuture.supplyAsync( () -> {
				return definitionProvider.calcDefinitions(model, params);
			});
		}
		
		return null;
	}



	@Override
	public CompletableFuture<List<ColorPresentation>> colorPresentation(ColorPresentationParams params) {
		
		C4DocumentModel model = c4Models.get(params.getTextDocument().getUri());
		
		if(model != null) {
			return CompletableFuture.supplyAsync( () -> {
				return colorProvider.calcColorPresentations(params.getColor());
			});
		}
		
		return null;

	}



	@Override
	public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
		
		String uri = params.getTextDocument().getUri();
		logger.info("semanticTokensFull " + uri);
		
		C4DocumentModel model = c4Models.get(params.getTextDocument().getUri());
		if(model != null) {
			List<Integer> tokens = semanticTokenProvider.calculateTokens(model);
			return CompletableFuture.supplyAsync( () -> {
				SemanticTokens semanticTokens = new SemanticTokens(tokens);
				return semanticTokens;
			});
		}

		return null;
	}



	@Override
	public CompletableFuture<Hover> hover(HoverParams params) {
		String uri = params.getTextDocument().getUri();
		logger.info("hover " + uri);
		
		C4DocumentModel model = c4Models.get(params.getTextDocument().getUri());
		
		if(model != null) {
			return CompletableFuture.supplyAsync( () -> {
				return hoverProvider.calcHover(model, params);
			});
		}
		
		return null;

	}

	@Override
	public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {

		String uri = params.getTextDocument().getUri();
		logger.info("codeLens " + uri);

		C4DocumentModel model = c4Models.get(params.getTextDocument().getUri());
		
		if(model != null) {
			return CompletableFuture.supplyAsync( () -> {
				return codeLenseProvider.calcCodeLenses(model);
			});
		}
		
		return null;
	}



	@Override
	public void didOpen(DidOpenTextDocumentParams params) {

		String uri = params.getTextDocument().getUri();
		logger.info("didOpen " + uri);

		List<Diagnostic> errors = calcDiagnostics(uri, params.getTextDocument().getText());

		CompletableFuture.runAsync(() -> ls.getClient().publishDiagnostics(new PublishDiagnosticsParams(uri, errors)));		

	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		
		String uri = params.getTextDocument().getUri();
		logger.info("didChange " + uri);
		
		List<Diagnostic> errors = calcDiagnostics(uri, params.getContentChanges().get(0).getText());
						
		CompletableFuture.runAsync(() -> ls.getClient().publishDiagnostics(new PublishDiagnosticsParams(uri, errors)));		
		
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		logger.info("didClose " + params.getTextDocument().getUri());
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		logger.info("didSave " + params.getTextDocument().getUri());
	}

	public List<Diagnostic> calcDiagnostics(String uri, String content) {
		
		logger.debug("calcDiagnostics");
		List<Diagnostic> errors = new ArrayList<>();
		C4DocumentModel model = new C4DocumentModel(content, uri);
		StructurizrDslParser parser = new StructurizrDslParser(model); 
		
		try {
			parser.parse(content);
		} catch (StructurizrDslParserException e) {
			logger.error("calcDiagnostics {}", e.getMessage());

			int startPos = C4Utils.findFirstNonWhitespace(e.getLine(), 0, true);
			int endPos = e.getLine().length();
			int row = e.getLineNumber()-1;
						
			Diagnostic diagnostic = new Diagnostic();
			diagnostic.setSeverity(DiagnosticSeverity.Error);
			diagnostic.setMessage(e.getMessage());
			diagnostic.setRange(new Range(new Position(row, startPos), new Position(row, endPos)));
			errors.add(diagnostic);
		}
		
		try {
			model.setWorkspace(parser.getWorkspace());
			model.setValid(errors.size() == 0);		
			c4Models.put(uri, model);	
		}
		catch(Exception e) {
			logger.error("Cannot parse workspace: {}", e.getMessage());
		}
		
		return errors;
	}
	
}
