package de.systemticks.c4dsl.ls.service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeLensOptions;
import org.eclipse.lsp4j.ExecuteCommandOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.commands.C4ExecuteCommandProvider;
import de.systemticks.c4dsl.ls.provider.C4SemanticTokenProvider;

public class C4LanguageServer implements LanguageServer, LanguageClientAware {

    private static final Logger logger = LoggerFactory.getLogger(C4LanguageServer.class);

	private LanguageClient client;
	private C4TextDocumentService documentService;
	private C4WorkspaceService workspaceService;
	private String renderer;

	public C4LanguageServer(String renderer) {
		this.documentService = new C4TextDocumentService(this);
		this.workspaceService = new C4WorkspaceService(documentService.getDocumentManager());
		this.renderer = renderer;
	}
	
	public String getRenderer() {
		return renderer;
	}

	@Override
	public void connect(LanguageClient client) {
		logger.info("connect");		
		this.client = client;
	}	

	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
		
		logger.info("initialize");		
		final InitializeResult res = new InitializeResult(new ServerCapabilities());
			//res.getCapabilities().setCompletionProvider(new CompletionOptions());
		res.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
		res.getCapabilities().setCodeLensProvider(new CodeLensOptions());
			//res.getCapabilities().setHoverProvider(Boolean.TRUE);
		res.getCapabilities().setColorProvider(Boolean.TRUE);
		res.getCapabilities().setDefinitionProvider(Boolean.TRUE);
		SemanticTokensWithRegistrationOptions semanticTokenOptions = new SemanticTokensWithRegistrationOptions();
		semanticTokenOptions.setFull(true);
		SemanticTokensLegend legend = new SemanticTokensLegend(C4SemanticTokenProvider.TOKEN_TYPES, C4SemanticTokenProvider.TOKEN_MODIFIERS);
		semanticTokenOptions.setLegend(legend);
		res.getCapabilities().setSemanticTokensProvider(semanticTokenOptions);
		res.getCapabilities().setExecuteCommandProvider(new ExecuteCommandOptions(
			Arrays.asList(C4ExecuteCommandProvider.EXPORT_FILE_TO_PUML, C4ExecuteCommandProvider.UPDATE_CONFIGURATION, C4ExecuteCommandProvider.CALCULATE_TEXT_DECORATIONS )));
			//res.getCapabilities().setWorkspace(new WorkspaceServerCapabilities(new WorkspaceFoldersOptions()));
				
		return CompletableFuture.supplyAsync(() -> res);
	}

	@Override
	public CompletableFuture<Object> shutdown() {
		logger.info("shutdown");
		return CompletableFuture.supplyAsync(() -> Boolean.TRUE);
	}

	@Override
	public void exit() {
		logger.info("exit");
		System.exit(0);
		
	}

	@Override
	public TextDocumentService getTextDocumentService() {
		logger.info("getTextDocumentService");
		return this.documentService;
	}

	@Override
	public WorkspaceService getWorkspaceService() {
		logger.info("getWorkspaceService");
		return this.workspaceService;
	}

	public LanguageClient getClient() {
		return client;
	}

}
