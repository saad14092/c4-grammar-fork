package de.systemticks.c4.utils

import de.systemticks.c4.c4Dsl.NamedElement
import de.systemticks.c4.c4Dsl.RelationShip
import de.systemticks.c4.c4Dsl.StyledElement
import de.systemticks.c4.c4Dsl.Workspace
import java.util.List
import org.eclipse.xtext.EcoreUtil2

class C4Utils {

	def static allRelationShips(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, RelationShip);		
	}

	def static allStyles(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, StyledElement);		
	}

	def static allNamedElements(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, NamedElement);		
	}

	def static allTags(Workspace workspace) {
		
		val allTags = newArrayList
			
		workspace.allNamedElements.forEach[
			allTags.addAll(tags)
		]
		
		allTags.toSet.toList					
	}

	def static List<String> getTags(NamedElement e) {
		(e.taglist === null) ? newArrayList : e.taglist.split(',')			
	}

	
}