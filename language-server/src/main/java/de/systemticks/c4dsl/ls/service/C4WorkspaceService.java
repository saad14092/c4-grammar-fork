package de.systemticks.c4dsl.ls.service;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.provider.C4ExecuteCommandProvider;

public class C4WorkspaceService implements WorkspaceService{

    private static final Logger logger = LoggerFactory.getLogger(C4WorkspaceService.class);
	private C4ExecuteCommandProvider commandProvider = new C4ExecuteCommandProvider();

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
		logger.info("executeCommand {}", params.getCommand());	
		commandProvider.execute( params.getCommand(), params.getArguments());

		return CompletableFuture.supplyAsync( () -> {
			return Boolean.TRUE;	
		});
		//return WorkspaceService.super.executeCommand(params);
	}
	
	

}
