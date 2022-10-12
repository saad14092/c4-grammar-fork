package de.systemticks.c4dsl.ls.service;

import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.systemticks.c4dsl.ls.commands.C4ExecuteCommandProvider;
import de.systemticks.c4dsl.ls.commands.C4ExecuteCommandResult;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C4WorkspaceService implements WorkspaceService{

    private static final Logger logger = LoggerFactory.getLogger(C4WorkspaceService.class);
	private C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();
	private C4TextDocumentService documentService;

	public C4WorkspaceService(C4TextDocumentService documentService) {
		this.documentService = documentService;
	}

	@Override
	public void didChangeConfiguration(DidChangeConfigurationParams params) {
		logger.info("didChangeConfiguration");
	}

	@Override
	public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
		logger.info("didChangeWatchedFiles");		
	}

	@Override
	public void didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams params) {
		logger.info("didChangeWorkspaceFolders");		
		WorkspaceService.super.didChangeWorkspaceFolders(params);
	}

	@Override
	public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
		
		return CompletableFuture.supplyAsync( () -> {
			logger.info("executeCommand {}", params.getCommand());
			if(params.getCommand().equals(C4ExecuteCommandProvider.CALCULATE_TEXT_DECORATIONS)) {
				JsonElement decorations = documentService.textDecorations((JsonObject) params.getArguments().get(0)) ;
				logger.info("decorations {}", decorations);
				return C4ExecuteCommandResult.TEXT_DECORATIONS.setResultData(decorations).toJson();
			}
			else {
				return commandProvider.execute( params.getCommand(), params.getArguments(), null).toJson();
			}
		});

	}
	

}
