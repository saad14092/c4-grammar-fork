package de.systemticks.c4dsl.ls.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import com.structurizr.dsl.StructurizrDslParserListener;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;
import com.structurizr.view.View;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4DocumentManager implements StructurizrDslParserListener {
    
    private static final Logger logger = LoggerFactory.getLogger(C4DocumentManager.class);

    private Map<String, C4DocumentModel> c4Models = new ConcurrentHashMap<>();

	private String currentFile;

    public C4DocumentModel getDocument(TextDocumentIdentifier documentId) throws URISyntaxException {
                    
		return getModel(new File(new URI(documentId.getUri())));
    }

	@Override
	public void onParsedRelationShip(File file, int lineNumber, String identifier, Relationship relationship) {
		if(relationship != null) {
			logger.debug("onParsedRelationShip {}->{}, identifier: {}, at linenumber: {}, {}", relationship.getSourceId(), relationship.getDestinationId(), identifier,  file.getName(), lineNumber);
			C4DocumentModel c4Model = getModel(file);
			c4Model.addRelationship(lineNumber, new C4ObjectWithContext<Relationship>(identifier, relationship, c4Model));
		}
		else {
			logger.error("onParsedRelationShip at linenumber {}, {}", file.getName(), lineNumber);
		}
	}

	@Override
	public void onParsedModelElement(File file, int lineNumber, String identifier, Element item) {
		logger.debug("onParsedModelElement identifier: {}, modelId: {} at linenumber: {}, {}", identifier, item.getId(), file.getName(), lineNumber);
		C4DocumentModel c4Model = getModel(file);
        c4Model.addElement(lineNumber, new C4ObjectWithContext<Element>(identifier, item, c4Model));
	}

	@Override
	public void onParsedView(File file, int lineNumber, View view) {
		logger.debug("onParsedView View: {} at linenumber {}, {}", view.getKey(), file.getName(), lineNumber);
        getModel(file).addView(lineNumber, view);
	}
	
	@Override
	public void onParsedColor(File file, int lineNumber) {
		logger.debug("onParsedColor at linenumber {}, {}", file.getName(), lineNumber);
        getModel(file).addColor(lineNumber);
	}
	
	
	@Override
	public void onEndContext(File file, int linenumber, String context) {
		logger.debug("onEndContext {} at linenumber {}, {}", context, file.getName(), linenumber);
	}

	
    @Override
	public void onInclude(File hostFile, int lineNumber, File referencedFile, String path) {
		logger.debug("onInclude: {} includes {} at linenumber {}", hostFile.getName(), referencedFile.getName(), lineNumber);
		getModel(hostFile).addReferencedModel(getModel(referencedFile), lineNumber, path);
	}

	private C4DocumentModel getModel(File _file) {

		String file = (currentFile != null && _file.getName().equals(".")) ? currentFile : _file.getAbsolutePath();

		logger.debug("getModel _file: {}, currentFile: {} -> {}", _file.getName(), currentFile, file);

        return c4Models.computeIfAbsent(file, (key) -> {
            try {
                logger.error("getModel - created through internal include {}", file);
                String content = new String(Files.readAllBytes( Paths.get(key)));
                C4DocumentModel model = new C4DocumentModel(content, file, true);
                return model;
            } 
            catch (IOException e) {
                logger.error("Cannot retrieve model {}", e.getMessage());
                return null;
            }
        });
    }

	private C4DocumentModel createModel(File file, String content) {

		currentFile = file.getAbsolutePath();

		logger.debug("createModel {}", currentFile);

		C4DocumentModel model = new C4DocumentModel(content, currentFile);

		return c4Models.compute(file.getAbsolutePath(), (k, v) -> model);	
	}

	public List<Diagnostic> calcDiagnostics(File file, String content) {

		logger.debug("calcDiagnostics");
		StructurizrDslParser parser = new StructurizrDslParser(this); 	
		List<Diagnostic> errors = new ArrayList<>();
		C4DocumentModel model = createModel(file, content);
		
		try {
			logger.debug("Parsing...");
			parser.parse(content, file);
			logger.debug("Parsing finished");
		} catch (StructurizrDslParserException e) {
			logger.debug("Parsing failed");
			logger.info("ParserException {}", e.getMessage());

			int startPos = C4Utils.findFirstNonWhitespace(e.getLine(), 0, true);
			int endPos = e.getLine().length();
			int row = e.getLineNumber()-1;
						
			Diagnostic diagnostic = new Diagnostic();
			diagnostic.setSeverity(DiagnosticSeverity.Error);
			diagnostic.setMessage(e.getMessage());
			diagnostic.setRange(new Range(new Position(row, startPos), new Position(row, endPos)));
			errors.add(diagnostic);
		} catch (Exception e) {
			logger.error("calcDiagnostics {}"+e.getMessage());
		}
		finally {
			model.setWorkspace(parser.getWorkspace());
			model.setValid(errors.size() == 0);		
		/*
			if(parser.getWorkspace() != null) {
				model.setWorkspace(parser.getWorkspace());
				model.setValid(errors.size() == 0);		
			}
			else {
				errors.clear();
				c4Models.entrySet().stream().forEach( e -> {
					e.getValue().getReferencedModels().stream().forEach( ref -> {
						if(ref.getUri().equals(file.toURI().toString())) {
							errors.addAll(calcDiagnostics(new File(e.getKey()), e.getValue().getRawText()));
						}
					});
				});	
			}
			*/
		}

		return errors;
	}

    
}
