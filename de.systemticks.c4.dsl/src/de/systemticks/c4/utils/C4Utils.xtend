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
		val builtIn = newArrayList(#[DEFAULT_PERSON_TAG, ELEMENT_TAG])
		(p.taglist === null) ? builtIn : (p.taglist.split(',') + builtIn).toList									
	}

	def static dispatch List<String> getTags(SoftwareSystem sys) {
		val builtIn = newArrayList(#[DEFAULT_SOFTWARE_SYSTEM_TAG, ELEMENT_TAG])
		(sys.taglist === null) ? builtIn : (sys.taglist.split(',')+builtIn).toList									
	}

	def static dispatch List<String> getTags(Container c) {
		val builtIn = newArrayList(#[DEFAULT_CONTAINER_TAG, ELEMENT_TAG])
		(c.taglist === null) ? builtIn : (c.taglist.split(',')+builtIn).toList									
	}

	def static dispatch List<String> getTags(Component cmp) {
		val builtIn = newArrayList(#[DEFAULT_COMPONENT_TAG, ELEMENT_TAG])
		(cmp.taglist === null) ? builtIn : (cmp.taglist.split(',')+builtIn).toList									
	}

	def static List<String> getCustomTags(NamedElement e) {
		(e.taglist === null) ? newArrayList : e.taglist.split(',')			
	}

	
}