// Copyright (c) 2020 systemticks GmbH
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package de.systemticks.c4.utils

import de.systemticks.c4.c4Dsl.C4DslFactory
import de.systemticks.c4.c4Dsl.Component
import de.systemticks.c4.c4Dsl.Container
import de.systemticks.c4.c4Dsl.NamedElement
import de.systemticks.c4.c4Dsl.Person
import de.systemticks.c4.c4Dsl.RelationShip
import de.systemticks.c4.c4Dsl.SoftwareSystem
import de.systemticks.c4.c4Dsl.StyleShape
import de.systemticks.c4.c4Dsl.StyledElement
import de.systemticks.c4.c4Dsl.Workspace
import java.util.List
import org.eclipse.xtext.EcoreUtil2
import de.systemticks.c4.c4Dsl.StyledRelationShip

class C4Utils {

	public final static String ELEMENT_TAG = "Element"
	public final static String DEFAULT_CONTAINER_TAG = "Container"
	public final static String DEFAULT_PERSON_TAG = "Person"
	public final static String DEFAULT_SOFTWARE_SYSTEM_TAG = "Software System"
	public final static String DEFAULT_COMPONENT_TAG = "Component"

	def static allRelationShips(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, RelationShip);		
	}

	def static allStyledElements(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, StyledElement);		
	}
	
	def static allStyledRelationships(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, StyledRelationShip);		
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
		(e.taglist === null) ? newArrayList : e.taglist.split(',').map[trim]			
	}

	def static List<String> getCustomTags(RelationShip r) {
		(r.taglist === null) ? newArrayList : r.taglist.split(',').map[trim]			
	}

	def static createDefaultStyleContainer() {
		C4DslFactory.eINSTANCE.createStyledElement => [
			backgroundColor = "#438dd5"
			color = "#ffffff"
			shape = StyleShape.BOX		
			tag = DEFAULT_CONTAINER_TAG					
		]
	}

	def static createDefaultStyleSoftwareSystem() {
		C4DslFactory.eINSTANCE.createStyledElement => [
			backgroundColor = "#1168bd"
			color = "#ffffff"
			shape = StyleShape.BOX
			tag = DEFAULT_SOFTWARE_SYSTEM_TAG			
		]
	}

	def static createDefaultStylePerson() {
		C4DslFactory.eINSTANCE.createStyledElement => [
			backgroundColor = "#08427b"
			color = "#ffffff"
			shape = StyleShape.BOX	
			tag = DEFAULT_PERSON_TAG								
		]
	}

	def static createDefaultStyleComponent() {
		C4DslFactory.eINSTANCE.createStyledElement => [
			backgroundColor = "#85bbf0"
			color = "#000000"
			shape = StyleShape.BOX
			tag = DEFAULT_COMPONENT_TAG						
		]
	}
	
	def static createDefaultStyleRelationship() {
		C4DslFactory.eINSTANCE.createStyledRelationShip => [
			color = "#707070"
			dashed = "false"
		]		
	}
	
	def static isDashed(StyledRelationShip r) {
		r.dashed !== null  && r.dashed.equals("true")
	}
}