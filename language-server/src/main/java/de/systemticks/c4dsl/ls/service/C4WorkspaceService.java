package de.systemticks.c4dsl.ls.service;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C4WorkspaceService implements WorkspaceService{

    private static final Logger logger = LoggerFactory.getLogger(C4WorkspaceService.class);

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
		// TODO Auto-generated method stub
		logger.info("didChangeWorkspaceFolders");		
		WorkspaceService.super.didChangeWorkspaceFolders(params);
	}
	
	

}
