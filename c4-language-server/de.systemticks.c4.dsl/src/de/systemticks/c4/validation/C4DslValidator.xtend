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
package de.systemticks.c4.validation

import de.systemticks.c4.c4Dsl.C4DslPackage
import de.systemticks.c4.c4Dsl.StyledElement
import de.systemticks.c4.c4Dsl.Workspace
import org.eclipse.xtext.validation.Check

import static extension de.systemticks.c4.utils.C4Utils.*

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
class C4DslValidator extends AbstractC4DslValidator {
	
	
	@Check
	def tagExistForApplyStyledElement(StyledElement styledElement) {
		if(!styledElement.eResource.allContents.filter(Workspace).head.allTags.contains(styledElement.tag)) {
			error('Style cannot be applied, Tag <'+styledElement.tag+"> is nor a default tag, nor a customer tag", 
					C4DslPackage.Literals.STYLED_ELEMENT__TAG,
					"Unknown Tag")			
		}		
	}
	
	@Check
	def uniqueStyledElement(StyledElement styledElement) {
		if(styledElement.eResource.allContents.filter(StyledElement).map[tag].filter[equals(styledElement.tag)].size > 1) {
			error('Style element is already defined', 
					C4DslPackage.Literals.STYLED_ELEMENT__TAG,
					"Already Defined Style")						
		}
	}
	
}
