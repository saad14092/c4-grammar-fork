package de.systemticks.c4dsl.ls.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.Workspace;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;
import com.structurizr.view.View;

public class C4DocumentModel {

	private String rawText;
	private Workspace workspace;
	private boolean valid;
	private final static String NEW_LINE = "\\r?\\n";
	private String lines[];
	
    private static final Logger logger = LoggerFactory.getLogger(C4DocumentModel.class);
    
    private Map<Integer, View> viewToLineNumber = new HashMap<>();
    private Map<Integer, C4ObjectWithContext<Element>> elementsToLineNumber = new HashMap<>();
    private Map<Integer, C4ObjectWithContext<Relationship>> relationShipsToLineNumber = new HashMap<>();
	private Map<Integer, String> includesToLineNumber = new HashMap<>();
    private List<Integer> colors = new ArrayList<>();
	private List<C4DocumentModel> referencedModels = new ArrayList<>();
	private String uri;
	private boolean parsedInternally;

	public C4DocumentModel(String rawText, String path) {
		this(rawText, path, false);
	}

	public C4DocumentModel(String rawText, String path, boolean parsedInternally) {		
		this.rawText = rawText;
		this.uri = new File(path).toURI().toString();
		lines = getRawText().split(NEW_LINE);
		this.parsedInternally = parsedInternally;
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
		
	public boolean isParsedInternally() {
		return parsedInternally;
	}

	public void setWorkspace(Workspace workspace) {
		logger.debug("setWorkspace {}", workspace.getName());
		this.workspace = workspace;
	}

	public void setValid(boolean valid) {
		logger.debug("setValid {}", valid);
		this.valid = valid;
	}

	public Set<Entry<Integer, View>> getAllViews() {
		return viewToLineNumber.entrySet();
	}
			
	public List<Integer> getColors() {
		return colors;
	}

	public Optional<View> getViewAtLineNumber(int lineNumber) {
		return Optional.ofNullable(viewToLineNumber.get(lineNumber));
	}

	public Optional<C4ObjectWithContext<Element>> getElementAtLineNumber(int lineNumber) {
		return Optional.ofNullable(elementsToLineNumber.get(lineNumber));
	}

	public Set<Entry<Integer, C4ObjectWithContext<Element>>> getAllElements() {
		return elementsToLineNumber.entrySet();
	}

	public Optional<C4ObjectWithContext<Relationship>> getRelationshipAtLineNumber(int lineNumber) {
		return Optional.ofNullable(relationShipsToLineNumber.get(lineNumber));
	}

	public Optional<String> getIncludeAtLineNumber(int lineNumber) {
		return Optional.ofNullable(includesToLineNumber.get(lineNumber));
	}

	public Set<Entry<Integer, C4ObjectWithContext<Relationship>>> getAllRelationships() {
		return relationShipsToLineNumber.entrySet();
	}

	public List<Entry<Integer, C4ObjectWithContext<Element>>> findElementsById(String id) {

		List<Set<Entry<Integer, C4ObjectWithContext<Element>>>> allElements = 
			referencedModels.stream().map( ref -> ref.elementsToLineNumber.entrySet()).collect(Collectors.toList());

		allElements.add(elementsToLineNumber.entrySet());

		return allElements.stream().flatMap(Collection::stream).filter( entry -> entry.getValue().getObject().getId().equals(id)).collect(Collectors.toList());
	}
	
	public String getLineAt(int lineNumber) {
		return lines[lineNumber];
	}

	public void addRelationship(int lineNumber, C4ObjectWithContext<Relationship> c4ObjectWithContext) {
		relationShipsToLineNumber.put(lineNumber, c4ObjectWithContext);
	}

	public void addElement(int lineNumber, C4ObjectWithContext<Element> c4ObjectWithContext) {
		elementsToLineNumber.put(lineNumber, c4ObjectWithContext);
	}

    public void addView(int lineNumber, View view) {
		viewToLineNumber.put(lineNumber, view);
    }

    public void addColor(int lineNumber) {
		colors.add(lineNumber);
    }

	private void addInclude(int lineNumber, String path) {
		includesToLineNumber.put(lineNumber, path);
	}

    public void addReferencedModel(C4DocumentModel referencedModel, int lineNumber, String path) {
		referencedModels.add(referencedModel);
		addInclude(lineNumber, path);
    }

	public List<C4DocumentModel> getReferencedModels() {
		return this.referencedModels;
	}

}
