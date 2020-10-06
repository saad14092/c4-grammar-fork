package de.systemticks.c4.utils

import de.systemticks.c4.c4Dsl.Component
import de.systemticks.c4.c4Dsl.Container
import de.systemticks.c4.c4Dsl.NamedElement
import de.systemticks.c4.c4Dsl.Person
import de.systemticks.c4.c4Dsl.RelationShip
import de.systemticks.c4.c4Dsl.SoftwareSystem
import de.systemticks.c4.c4Dsl.StyledElement
import de.systemticks.c4.c4Dsl.Workspace
import java.util.List
import org.eclipse.xtext.EcoreUtil2

class C4Utils {

	public final static String ELEMENT_TAG = "Element"
	public final static String DEFAULT_CONTAINER_TAG = "Container"
	public final static String DEFAULT_PERSON_TAG = "Person"
	public final static String DEFAULT_SOFTWARE_SYSTEM_TAG = "Software System"
	public final static String DEFAULT_COMPONENT_TAG = "Component"

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
		
		val allTags = newArrayList(#[ELEMENT_TAG, DEFAULT_CONTAINER_TAG, DEFAULT_COMPONENT_TAG, DEFAULT_PERSON_TAG, DEFAULT_SOFTWARE_SYSTEM_TAG])
			
		workspace.allNamedElements.forEach[
			allTags.addAll(customTags)
		]
		
		allTags.toSet.toList 					
	}
	
	def static dispatch List<String> getTags(Person p) {
		(p.customTags + newArrayList(#[DEFAULT_PERSON_TAG, ELEMENT_TAG])).toList
	}

	def static dispatch List<String> getTags(SoftwareSystem sys) {
		(sys.customTags + newArrayList(#[DEFAULT_SOFTWARE_SYSTEM_TAG, ELEMENT_TAG])).toList
	}

	def static dispatch List<String> getTags(Container c) {
		(c.customTags + newArrayList(#[DEFAULT_CONTAINER_TAG, ELEMENT_TAG])).toList
	}

	def static dispatch List<String> getTags(Component cmp) {
		(cmp.customTags + newArrayList(#[DEFAULT_COMPONENT_TAG, ELEMENT_TAG])).toList
	}

	def static List<String> getCustomTags(NamedElement e) {
		(e.taglist === null) ? newArrayList : e.taglist.split(',')			
	}

	
}