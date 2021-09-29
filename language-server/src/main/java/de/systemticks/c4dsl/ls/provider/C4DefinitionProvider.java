package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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
import com.structurizr.model.SoftwareSystemInstance;
import com.structurizr.view.View;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.model.C4WithId;
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
			String referencedId = C4Utils.getIdentifierOfView(view);
			if(referencedId != null && !referencedId.equals(IDENTIFIER_WILDCARD)) {
				Location location = findModelElementById(c4Model, referencedId, params);
				if(location != null) {
					locations.add(location);
				}
			}
		}
		C4WithId<Relationship> relationship = c4Model.getRelationshipAtLineNumber(currentLineNumner);
		if(relationship != null) {
			logger.debug("Selected Line has relationship {}, {}", relationship.getObject().getSourceId(), relationship.getObject().getDestinationId());
			String sourceId = relationship.getObject().getSourceId();
			if(sourceId != null && !sourceId.equals(IDENTIFIER_WILDCARD)) {
				Location location = findModelElementById(c4Model, sourceId, params);
				if(location != null) {
					locations.add(location);
				}
			}
			String destinationId = relationship.getObject().getDestinationId();
			if(destinationId != null && !destinationId.equals(IDENTIFIER_WILDCARD)) {
				Location location = findModelElementById(c4Model, destinationId, params);
				if(location != null) {
					locations.add(location);
				}
			}
		}
		C4WithId<Element> element = c4Model.getElementAtLineNumber(currentLineNumner);
		if(element != null) {
			if(element.getObject() instanceof ContainerInstance) {
				String containerId = ((ContainerInstance) element.getObject()).getContainerId();
				if(containerId != null && !containerId.equals(IDENTIFIER_WILDCARD)) {
					Location location = findModelElementById(c4Model, containerId, params);
					if(location != null) {
						locations.add(location);
					}
				}
			}
			else if(element.getObject() instanceof SoftwareSystemInstance) {
				String softwareSystemId = ((SoftwareSystemInstance) element.getObject()).getSoftwareSystemId();
				if(softwareSystemId != null && !softwareSystemId.equals(IDENTIFIER_WILDCARD)) {
					Location location = findModelElementById(c4Model, softwareSystemId, params);
					if(location != null) {
						locations.add(location);
					}
				}
			}
		}

		return Either.forLeft(locations);
	}

	private Location findModelElementById(C4DocumentModel c4Model, String id, DefinitionParams params) {

		List<Entry<Integer, C4WithId<Element>>> refs = c4Model.findElementsById(id);
		if(refs.size() == 1) {
			int refLineNumber = refs.get(0).getKey();
			C4WithId<Element> element = c4Model.getElementAtLineNumber(refLineNumber);
			logger.debug("Found referenced element in line {} for usage in line {}", refLineNumber, params.getPosition().getLine());
			logger.debug("    Details: {}",element.getIdentifier());
			final int startPos = C4Utils.getStartPosition(c4Model.getLineAt(params.getPosition().getLine()), element.getIdentifier());
			if(startPos == C4Utils.NOT_FOUND_WITHIN_STRING) {
				logger.error("Identifier {} not found in line {} ", element.getIdentifier(), params.getPosition().getLine());
				return null;
			}
			final int endPos = startPos + element.getIdentifier().length();
			if(params.getPosition().getCharacter() >= startPos && params.getPosition().getCharacter() <= endPos) {
				logger.debug("    Cursor {} within range [{}, {}]", params.getPosition().getCharacter(), startPos, endPos);			
				return createLocationForReferencedIdentifier(c4Model, refLineNumber-1, element.getIdentifier());
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
	
}
