package de.systemticks.c4dsl.ls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.Color;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentColorParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.provider.CodeLenseProvider;
import de.systemticks.c4dsl.ls.provider.HoverProvider;
import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4TextDocumentService implements TextDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(C4TextDocumentService.class);

	private C4LanguageServer ls;

	private CodeLenseProvider codeLenseProvider = new CodeLenseProvider();
	private HoverProvider hoverProvider = new HoverProvider();
	
	private Map<String, C4DocumentModel> c4Models = new HashMap<>();
	
	public C4TextDocumentService(C4LanguageServer c4LanguageServer) {
		this.ls = c4LanguageServer;
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
		logger.info("didClose" + params.getTextDocument().getUri());
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		logger.info("didSave" + params.getTextDocument().getUri());
	}

	public List<Diagnostic> calcDiagnostics(String uri, String content) {
		
		logger.debug("calcDiagnostics");
		List<Diagnostic> errors = new ArrayList<>();
		C4DocumentModel model = new C4DocumentModel(content);
		StructurizrDslParser parser = new StructurizrDslParser(model); 
//		StructurizrDslParser parser = new StructurizrDslParser(); 
		
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
		
		model.setWorkspace(parser.getWorkspace());
		model.setValid(errors.size() == 0);		
		c4Models.put(uri, model);
		
		return errors;
	}
	
}
