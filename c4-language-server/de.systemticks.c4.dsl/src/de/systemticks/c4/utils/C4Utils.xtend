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

import de.systemticks.c4.c4Dsl.BasicModelElement
import de.systemticks.c4.c4Dsl.Component
import de.systemticks.c4.c4Dsl.Container
import de.systemticks.c4.c4Dsl.Person
import de.systemticks.c4.c4Dsl.RelationShip
import de.systemticks.c4.c4Dsl.SoftwareSystem
import de.systemticks.c4.c4Dsl.StyledElement
import de.systemticks.c4.c4Dsl.StyledRelationShip
import de.systemticks.c4.c4Dsl.Workspace
import java.util.List
import org.eclipse.xtext.EcoreUtil2
import de.systemticks.c4.c4Dsl.AnyModelElement

class C4Utils {

	public final static String ELEMENT_TAG = "Element"
	public final static String DEFAULT_PERSON_TAG = "Person"
	public final static String DEFAULT_SOFTWARE_SYSTEM_TAG = "Software System"
	public final static String DEFAULT_CONTAINER_TAG = "Container"
	public final static String DEFAULT_COMPONENT_TAG = "Component"
	public final static String DEFAULT_DEPLOYMENT_NODE_TAG = "Deployment Node"
	public final static String DEFAULT_INFRASTRUCTURE_NODE_TAG = "Infrastructure Node"
	public final static String DEFAULT_SOFTWARE_SYSTEM_INSTANCE_TAG = "Software System Instance"
	public final static String DEFAULT_CONTAINER_INSTANCE_TAG = "Container Instance"

	def static allRelationShips(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, RelationShip);		
	}

	def static allStyledElements(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, StyledElement);		
	}
	
	def static allStyledRelationships(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, StyledRelationShip);		
	}

	def static allAnyModelElements(Workspace workspace) {
	    return EcoreUtil2.getAllContentsOfType(workspace, AnyModelElement);		
	}

	def static allTags(Workspace workspace) {
		
		val allTags = newArrayList(#[
			ELEMENT_TAG, DEFAULT_CONTAINER_TAG, DEFAULT_COMPONENT_TAG, DEFAULT_PERSON_TAG, DEFAULT_SOFTWARE_SYSTEM_TAG,
			DEFAULT_DEPLOYMENT_NODE_TAG, DEFAULT_INFRASTRUCTURE_NODE_TAG, DEFAULT_SOFTWARE_SYSTEM_INSTANCE_TAG, DEFAULT_CONTAINER_INSTANCE_TAG
		])
			
		workspace.allAnyModelElements.forEach[
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

	def static List<String> getCustomTags(AnyModelElement e) {
		(e.taglist === null) ? newArrayList : e.taglist.split(',').map[trim]			
	}

	def static List<String> getCustomTags(RelationShip r) {
		(r.taglist === null) ? newArrayList : r.taglist.split(',').map[trim]			
	}
	
	def static isDashed(StyledRelationShip r) {
		r.dashed !== null  && r.dashed.equals("true")
	}
		
}