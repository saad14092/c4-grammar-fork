package de.systemticks.c4dsl.ls.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Map<Integer, C4ObjectWithContext<Element>> elementsToLineNumber = new HashMap<>();
    private Map<Integer, C4ObjectWithContext<Relationship>> relationShipsToLineNumber = new HashMap<>();
    private List<Integer> colors = new ArrayList<>();
	private String uri;

	public C4DocumentModel(String rawText, String uri) {
		this.rawText = rawText;
		this.uri = uri;
		lines = getRawText().split(NEW_LINE);
	}

	public String getRawText() {
		return rawText;
	}

	public Workspace getWorkspace() {		
		return workspace;
	}

	public String getUri() {
		return uri;
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
		if(relationship != null) {
			logger.debug("onParsedRelationShip {}->{}, identifier: {}, at linenumber: {}", relationship.getSourceId(), relationship.getDestinationId(), identifier, lineNumber);
			relationShipsToLineNumber.put(lineNumber, new C4ObjectWithContext<Relationship>(identifier, relationship));	
		}
		else {
			logger.error("onParsedRelationShip in line {}", lineNumber);
		}
	}

	@Override
	public void onParsedModelElement(int lineNumber, String identifier, Element item) {
		logger.debug("onParsedModelElement identifier: {}, modelId: {} at linenumber: {}", identifier, item.getId(), lineNumber);
		elementsToLineNumber.put(lineNumber, new C4ObjectWithContext<Element>(identifier, item));
	}

	@Override
	public void onParsedView(int lineNumber, View view) {
		logger.debug("onParsedView View: {} at linenumber {}", view.getKey(), lineNumber);
		viewToLineNumber.put(lineNumber, view);
	}
	
	@Override
	public void onParsedColor(int lineNumber) {
		logger.debug("onParsedColor at linenumber {}", lineNumber);
		colors.add(lineNumber);
	}
	
	
	@Override
	public void onEndContext(int linenumber, String context) {
		logger.debug("onEndContext {} at linenumber {}", context, linenumber);
	}

	public Set<Entry<Integer, View>> getAllViews() {
		return viewToLineNumber.entrySet();
	}
			
	public List<Integer> getColors() {
		return colors;
	}

	public View getViewAtLineNumber(int lineNumber) {
		return viewToLineNumber.get(lineNumber);
	}

	public C4ObjectWithContext<Element> getElementAtLineNumber(int lineNumber) {
		return elementsToLineNumber.get(lineNumber);
	}

	public Set<Entry<Integer, C4ObjectWithContext<Element>>> getAllElements() {
		return elementsToLineNumber.entrySet();
	}

	public C4ObjectWithContext<Relationship> getRelationshipAtLineNumber(int lineNumber) {
		return relationShipsToLineNumber.get(lineNumber);
	}

	public Set<Entry<Integer, C4ObjectWithContext<Relationship>>> getAllRelationships() {
		return relationShipsToLineNumber.entrySet();
	}

	public List<Entry<Integer, C4ObjectWithContext<Element>>> findElementsById(String id) {
		return elementsToLineNumber.entrySet().stream().filter( entry -> entry.getValue().getObject().getId().equals(id)).collect(Collectors.toList());
	}
	
	public String getLineAt(int lineNumber) {
		return lines[lineNumber];
	}
}
