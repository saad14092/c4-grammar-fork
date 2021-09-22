package de.systemticks.c4dsl.ls.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParserListener;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;
import com.structurizr.view.View;

public class C4DocumentModel implements StructurizrDslParserListener {

	private String rawText;
	private Workspace workspace;
	private boolean valid;
	private final static String NEW_LINE = "\\r?\\n";
	private String lines[];
	
    private static final Logger logger = LoggerFactory.getLogger(C4DocumentModel.class);
    
    private Map<Integer, View> viewToLineNumber = new HashMap<>();
    private Map<Integer, Element> elementsToLineNumber = new HashMap<>();
    private Map<Integer, Relationship> relationShipsToLineNumber = new HashMap<>();

	public C4DocumentModel(String rawText) {
		this.rawText = rawText;
		lines = getRawText().split(NEW_LINE);
	}

	public String getRawText() {
		return rawText;
	}

	public Workspace getWorkspace() {		
		return workspace;
	}

	public boolean isValid() {
		return valid;
	}
	
	public void setWorkspace(Workspace workspace) {
		logger.debug("setWorkspace {}", workspace.getName());
		this.workspace = workspace;
	}

	public void setValid(boolean valid) {
		logger.debug("setValid {}", valid);
		this.valid = valid;
	}

	@Override
	public void onParsedRelationShip(int lineNumber, String identifier, Relationship relationship) {
		logger.debug("onParsedRelationShip at {}", lineNumber);
		relationShipsToLineNumber.put(lineNumber, relationship);
	}

	@Override
	public void onParsedModelElement(int lineNumber, String identifier, Element item) {
		logger.debug("onParsedModelElement {}, {} at {}", identifier, item.getId(), lineNumber);
		elementsToLineNumber.put(lineNumber, item);
	}

	@Override
	public void onParsedView(int lineNumber, View view) {
		logger.debug("onParsedView View: {} at {}", view.getKey(), lineNumber);
		viewToLineNumber.put(lineNumber, view);
	}
	
	public Set<Entry<Integer, View>> getViewToLineNumbers() {
		return viewToLineNumber.entrySet();
	}
	
	public View getViewAtLineNumber(int lineNumber) {
		return viewToLineNumber.get(lineNumber);
	}
	
	public String getLineAt(int lineNumber) {
		return lines[lineNumber];
	}
}
