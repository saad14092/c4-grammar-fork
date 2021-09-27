package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.model.ContainerInstance;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.DynamicView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.View;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4DefinitionProvider {

    private static final Logger logger = LoggerFactory.getLogger(C4DefinitionProvider.class);
	private static final String IDENTIFIER_WILDCARD = "*";

	public Either<List<? extends Location>, List<? extends LocationLink>> calcDefinitions(C4DocumentModel c4Model, DefinitionParams params) {

		int currentLineNumner = params.getPosition().getLine()+1;

		List<Location> locations = new ArrayList<>();
		logger.debug("calcDefinitions for line {}", currentLineNumner);

		// search for references in views
		View view = c4Model.getViewAtLineNumber(currentLineNumner);
		if(view != null) {
			String referencedId = getIdentifierOfView(view);
			if(referencedId != null && !referencedId.equals(IDENTIFIER_WILDCARD)) {
				Location location = findModelElementById(c4Model, referencedId, params);
				if(location != null) {
					locations.add(location);
				}
			}
		}
		Relationship relationship = c4Model.getRelationshipAtLineNumber(currentLineNumner);
		if(relationship != null) {
			logger.debug("Selected Line has relationship {}, {}", relationship.getSourceId(), relationship.getDestinationId());
			String sourceId = relationship.getSourceId();
			if(sourceId != null && !sourceId.equals(IDENTIFIER_WILDCARD)) {
				Location location = findModelElementById(c4Model, sourceId, params);
				if(location != null) {
					locations.add(location);
				}
			}
			String destinationId = relationship.getDestinationId();
			if(destinationId != null && !destinationId.equals(IDENTIFIER_WILDCARD)) {
				Location location = findModelElementById(c4Model, destinationId, params);
				if(location != null) {
					locations.add(location);
				}
			}
		}
		Entry<String, Element> element = c4Model.getElementAtLineNumber(currentLineNumner);
		if(element != null) {
			if(element.getValue() instanceof ContainerInstance) {
				String containerId = ((ContainerInstance) element.getValue()).getContainerId();
				if(containerId != null && !containerId.equals(IDENTIFIER_WILDCARD)) {
					Location location = findModelElementById(c4Model, containerId, params);
					if(location != null) {
						locations.add(location);
					}
				}
			}
		}

		return Either.forLeft(locations);
	}

	private Location findModelElementById(C4DocumentModel c4Model, String id, DefinitionParams params) {

		List<Entry<Integer, Entry<String, Element>>> refs = c4Model.findElementsById(id);
		if(refs.size() == 1) {
			int refLineNumber = refs.get(0).getKey();
			Entry<String, Element> element = c4Model.getElementAtLineNumber(refLineNumber);
			logger.debug("Found referenced element in line {} for usage in line {}", refLineNumber, params.getPosition().getLine());
			logger.debug("    Details: {}",element.getKey());
			final int startPos = C4Utils.getStartPosition(c4Model.getLineAt(params.getPosition().getLine()), element.getKey());
			if(startPos == C4Utils.NOT_FOUND_WITHIN_STRING) {
				logger.error("Identifier {} not found in line {} ", element.getKey(), params.getPosition().getLine());
				return null;
			}
			final int endPos = startPos + element.getKey().length();
			if(params.getPosition().getCharacter() >= startPos && params.getPosition().getCharacter() <= endPos) {
				logger.debug("    Cursor {} within range [{}, {}]", params.getPosition().getCharacter(), startPos, endPos);			
				return createLocationForReferencedIdentifier(c4Model, refLineNumber-1, element.getKey());
			}
			else {
				logger.debug("    Cursor {} out of range [{}, {}]", params.getPosition().getCharacter(), startPos, endPos);			
			}
		}

		return null;
	}

	private Location createLocationForReferencedIdentifier(C4DocumentModel c4Model, int lineNumber, String referencedId) {

		Location location = new Location();

		final int refStartPos = c4Model.getLineAt(lineNumber).indexOf(referencedId);
		final int refEndPos = refStartPos + referencedId.length();
		logger.debug("    Reference Found at linenumber {}, in range [{}, {}]", lineNumber, refStartPos, refEndPos);
		location.setRange(new Range( new Position(lineNumber,refStartPos), new Position(lineNumber,refEndPos)));
		location.setUri(c4Model.getUri());

		return location;

	}
	
	private String getIdentifierOfView(View view) {

		if(view instanceof ContainerView || view instanceof SystemContextView || view instanceof DeploymentView) {
			return view.getSoftwareSystemId();
		}
		else if(view instanceof ComponentView) {
			return ((ComponentView)view).getContainerId();
		}
		else if(view instanceof DynamicView) {
			return ((DynamicView)view).getElementId();
		}

		return null;

	}
}
