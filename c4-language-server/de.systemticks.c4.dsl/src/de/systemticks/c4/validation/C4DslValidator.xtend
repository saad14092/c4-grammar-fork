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

import de.systemticks.c4.c4Dsl.AdditionalData
import de.systemticks.c4.c4Dsl.AnyModelElement
import de.systemticks.c4.c4Dsl.BasicModelElement
import de.systemticks.c4.c4Dsl.C4DslPackage
import de.systemticks.c4.c4Dsl.Component
import de.systemticks.c4.c4Dsl.Constant
import de.systemticks.c4.c4Dsl.Container
import de.systemticks.c4.c4Dsl.DeploymentElement
import de.systemticks.c4.c4Dsl.DeploymentEnvironment
import de.systemticks.c4.c4Dsl.DeploymentNode
import de.systemticks.c4.c4Dsl.Group
import de.systemticks.c4.c4Dsl.Import
import de.systemticks.c4.c4Dsl.Model
import de.systemticks.c4.c4Dsl.Person
import de.systemticks.c4.c4Dsl.RelationShip
import de.systemticks.c4.c4Dsl.SoftwareSystem
import de.systemticks.c4.c4Dsl.StyledElement
import de.systemticks.c4.c4Dsl.StyledRelationShip
import de.systemticks.c4.c4Dsl.Theme
import de.systemticks.c4.c4Dsl.View
import de.systemticks.c4.c4Dsl.Workspace
import de.systemticks.c4.themes.model.ThemeLoader
import de.systemticks.c4.themes.model.ThemeModelElement
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.regex.Pattern
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.validation.Check

import static extension de.systemticks.c4.utils.C4Utils.*

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
class C4DslValidator extends AbstractC4DslValidator {

	val SUBSTITUTE_PATTERN = Pattern.compile("\\$\\{.*\\}")	
	val COLOR_REGEX = "#[0-9A-Fa-f]{6}"
	
	public val static ISSUE_CODE_DUPLICATED_ELEMENT = "Already Defined Element"
	public val static ISSUE_CODE_ILLEGAL_CHARS_IN_NAME = "Name contains illegal characters"
	
	public static String NO_STYLED_ELEMENT_FOR_TAG = "No styled element for given tag"
	val themeMap = new HashMap<String, Map<String, ThemeModelElement>>
	
	@Check
	def checkShape(StyledElement styledElement) {
		if(styledElement.shape !== null && !shapes.map[toLowerCase].contains(styledElement.shape.toLowerCase)) {
			error('A shape with name '+styledElement.shape+' is not defined', 
					C4DslPackage.Literals.STYLED_ELEMENT__SHAPE,
					"Undefined Shape")						
		}
	}
	
	@Check
	def tagExistForApplyStyledElement(StyledElement styledElement) {
		if(!styledElement.eResource.allContents.filter(Workspace).head.allTags.contains(styledElement.tag)) {
			info('Style cannot be applied, Tag <'+styledElement.tag+"> is neither a default tag, nor a customer tag", 
					C4DslPackage.Literals.STYLED_ELEMENT__TAG,
					"Unknown Tag")			
		}		
	}
	
	@Check
	def includeExists(Import _import) {
		val dir = new File(_import.eResource.URI.toFileString).parent
		val dslFile = new File(dir + File.separator + _import.importURI.replace('"','') ) 
		
		if (!dslFile.exists || !dslFile.isFile) {
			error('No dsl file with name '+_import.importURI+" exists",
				C4DslPackage.Literals.IMPORT__IMPORT_URI,
				"Unknown Import"
			)			
		}
		
	}
	
	@Check
	def pathExists(AdditionalData docsOrAdrs) {
		val dir = new File(docsOrAdrs.eResource.URI.toFileString).parent 
		if(! (new File(dir + File.separator + docsOrAdrs.path).exists) ) {
			error('The path '+docsOrAdrs.path+" does not exists",
				C4DslPackage.Literals.ADDITIONAL_DATA__PATH,
				"Unknown Directory"
			)
		}
	}
	
	@Check
	def isImpliedRelationShipAllowed(RelationShip r) {
		val _model = r.eResource.allContents.filter(Model).head
		if(_model?.implied?.active.equals('false') && r.from === null) {
			error('Implied Relationships are set to "false". Please change to "true"', 
				C4DslPackage.Literals.RELATION_SHIP__TO,
				"Implied Relationship Setting Violation")														
		}		 		
	}
	
	@Check
	def relationshipBetweenParentAndChildren(RelationShip r) {
		if(r.from === null) {
			val parent = r.eContainer
			switch parent {
				SoftwareSystem : {
					if(parent.container.contains(r.to)) {
						error('Relationships cannot be added between parents and children', 
							C4DslPackage.Literals.RELATION_SHIP__TO,
							"Relationship not allowed ")																				
					}					
				} 
				Container : {
					if(parent.components.contains(r.to)) {
						error('Relationships cannot be added between parents and children', 
							C4DslPackage.Literals.RELATION_SHIP__TO,
							"Relationship not allowed ")																				
					}										
				}
				DeploymentNode : {
					if(parent.deploymentNodes.contains(r.to)) {
						error('Relationships cannot be added between parents and children', 
							C4DslPackage.Literals.RELATION_SHIP__TO,
							"Relationship not allowed ")																				
					}										
				}
				DeploymentEnvironment : {
					if(parent.deploymentNodes.contains(r.to)) {
						error('Relationships cannot be added between parents and children', 
							C4DslPackage.Literals.RELATION_SHIP__TO,
							"Relationship not allowed ")																				
					}										
				}
			}
		}
	}
		
	@Check(NORMAL)
	def styledElementsExistsForTag(AnyModelElement modelElement) {
		
		val styledTags = modelElement.eResource.allContents.filter(StyledElement).map[tag]
		val themes = modelElement.eResource.allContents.filter(Theme).toList
		
		modelElement.customTags.forEach[
			tag | {
				if( !styledTags.contains(tag)) {					
					// Not Style defined in the model. Maybe in a theme?
					//FIXME This is probably an expensive check.					
					themes.updateThemeMap
					val tagInTheme = themeMap.entrySet.findFirst[entry|
						entry.value.containsKey(tag)
					]
					// Style not found. Neither as a style element nor in a referenced theme					
					if(tagInTheme === null) {
						info('No Style with tag <'+tag+ '> defined yet', 
							C4DslPackage.Literals.ANY_MODEL_ELEMENT__TAGLIST,
							NO_STYLED_ELEMENT_FOR_TAG)														
					}					
				}
			}	
		]
	}
	
	private def updateThemeMap(List<Theme> themes) {
		themes.forEach[t|
			t.urls.forEach[u|
				if(!themeMap.containsKey(u)) {
					val loader = new ThemeLoader
					val themeModel = loader.loadFromURL(new URL(u))
					themeMap.put(u, loader.toMap(themeModel))
				}
			]
		]
	}
	

//	@Check(NORMAL)
	def stringSubstitutionWorkspace(Workspace workspace) {
		if(workspace.name !== null && workspace.name.length > 3 && workspace.name.hasUnknownConstant(workspace.constants)) {
			info('Workspace name contains a constant', 
				C4DslPackage.Literals.WORKSPACE__NAME,
				"Contains Constant")											
		}
	}
	
	def boolean hasUnknownConstant(String text, List<Constant> constants) {
		
		if(text !== null && text.length > 3) {
			val m = SUBSTITUTE_PATTERN.matcher(text)
			while(m.find) {
				if( !constants.map[name].contains( text.substring(m.start+2, m.end-1) ) ) {
					return true
				}
			}
		}

		return false		
	}
	
	
	@Check
	def uniqueStyledElement(StyledElement styledElement) {
		if(styledElement.eResource.allContents.filter(StyledElement).map[tag].filter[equals(styledElement.tag)].size > 1) {
			error('Style element is already defined', 
					C4DslPackage.Literals.STYLED_ELEMENT__TAG,
					ISSUE_CODE_DUPLICATED_ELEMENT)						
		}
	}
	
	@Check	
	def opacityValueRange(StyledElement styledElement) {
		if(styledElement.opacity < 0 || styledElement.opacity > 100) {
			error('Value for opacity must be between 0 and 100', 
					C4DslPackage.Literals.STYLED_ELEMENT__OPACITY,
					"Invalid Value Range")									
		} 
	}

	@Check
	def uniqueNamedElement(AnyModelElement anyModelElement) {
		if(anyModelElement.eResource.allContents.filter(BasicModelElement).map[name].filter[equals(anyModelElement.name)].size > 1) {
			error('Element with the id '+anyModelElement.name+' is already defined', 
					C4DslPackage.Literals.ANY_MODEL_ELEMENT__NAME,
					ISSUE_CODE_DUPLICATED_ELEMENT)						
		}
	}

	private def <T extends EObject> getSiblings(EObject element, Class<T> type) {
		element.eContainer.eContents.filter(type)
	}

	@Check
	def uniqueLabelElementPerson(Person person) {		
				
		if( person.getSiblings(Person).map[label].filter[equals(person.label)].size > 1) {
			error('Person with the label '+person.label+' is already defined in this model', 
					C4DslPackage.Literals.BASIC_MODEL_ELEMENT__LABEL,
					ISSUE_CODE_DUPLICATED_ELEMENT)						
		}
	}
	
	@Check
	def uniqueLabelElementSoftwareSystem(SoftwareSystem system) {
		if( system.getSiblings(SoftwareSystem).map[label].filter[equals(system.label)].size > 1) {
			error('SoftwareSystem with the label '+system.label+' is already defined in this model', 
					C4DslPackage.Literals.BASIC_MODEL_ELEMENT__LABEL,
					ISSUE_CODE_DUPLICATED_ELEMENT)						
		}
	}

	@Check
	def uniqueLabelElementComponent(Component component) {
		if( component.getSiblings(Component).map[label].filter[equals(component.label)].size > 1) {
			error('Component with the label '+component.label+' is already defined in this container', 
					C4DslPackage.Literals.BASIC_MODEL_ELEMENT__LABEL,
					ISSUE_CODE_DUPLICATED_ELEMENT)						
		}
	}

	@Check
	def uniqueLabelElementContainer(Container container) {
		if( container.getSiblings(Container).map[label].filter[equals(container.label)].size > 1) {
			error('Container with the label '+container.label+' is already defined in this SoftwareSystem', 
					C4DslPackage.Literals.BASIC_MODEL_ELEMENT__LABEL,
					ISSUE_CODE_DUPLICATED_ELEMENT)						
		}
	}

//	@Check
//	def uniqueElementsInSoftwareSystem(SoftwareSystem system) {
//		system.container.groupBy[label].forEach[label, list, index | 
//			if(list.size > 1) {
//			error('Container with the label '+label+' is already defined in this SoftwareSystem', 
//					C4DslPackage.Literals.SOFTWARE_SYSTEM__CONTAINER, 
//					index,
//					"Already Defined Element")						
//			}
//		]
//	}

	@Check
	def uniqueView(View view) {
		if(view.eResource.allContents.filter(View).filter[name !== null].map[name].filter[equals(view.name)].size > 1) {
			error('A View with the name'+view.name+' is already defined', 
					C4DslPackage.Literals.VIEW__NAME,
					ISSUE_CODE_DUPLICATED_ELEMENT)						
		}
	}

	@Check
	def validRelationShips(RelationShip r) {
		if(r.from instanceof BasicModelElement && !(r.to instanceof BasicModelElement)) {
			error('Relationship only allowed to elements of type Person, Software System, Container, Component', 
					C4DslPackage.Literals.RELATION_SHIP__TO,
					"Relationship not allowed")									
		}
		else if(r.from instanceof DeploymentNode && !(r.to instanceof DeploymentNode)) {
			error('Relationship only allowed to another Deployment Node', 
					C4DslPackage.Literals.RELATION_SHIP__TO,
					"Relationship not allowed")												
		}
		else if(r.from instanceof DeploymentElement && !(r.to instanceof DeploymentElement)) {
			error('Relationship only allowed to elements of type Deployment Node, Infrastructure Node, Software System Instance, Container Instance', 
					C4DslPackage.Literals.RELATION_SHIP__TO,
					"Relationship not allowed")												
		}
	}

	@Check	
	def allowedGroups(Group group) {
		
		if(group.eContainer instanceof Container) {
			group.element.forEach[ e, index |
				if(!(e instanceof Component)) {
					error('In the context of this group only components are allowed', 
						C4DslPackage.Literals.GROUP__ELEMENT, index,
						"Forbidden group element")											
				}
			]
		}
		else if(group.eContainer instanceof SoftwareSystem) {
			group.element.forEach[ e, index | 
				if(!(e instanceof Container)) {
					error('In the context of this group only containers are allowed', 
						C4DslPackage.Literals.GROUP__ELEMENT, index, 
						"Forbidden group element")											
				}
			]
		}
		else if(group.eContainer instanceof Model) {
			group.element.forEach[ e, index |
				if(!(e instanceof SoftwareSystem || e instanceof Person)) {
					error('In the context of this group only softwaresystems and persons are allowed', 
						C4DslPackage.Literals.GROUP__ELEMENT, index,
						"Forbidden group element")											
				}
			]
		}
		
	}

	@Check	
	def opacityValueRange(StyledRelationShip styledRelationShip) {
		if(styledRelationShip.opacity < 0 || styledRelationShip.opacity > 100) {
			error('Value for opacity must be between 0 and 100', 
					C4DslPackage.Literals.STYLED_RELATION_SHIP__POSITION,
					"Invalid Value Range")									
		}
		if(styledRelationShip.position < 0 || styledRelationShip.position > 100) {
			error('Value for position must be between 0 and 100', 
					C4DslPackage.Literals.STYLED_RELATION_SHIP__POSITION,
					"Invalid Value Range")									
		} 
		 
	}
	
	val INVALID_COLOR_MESSAGE = 'Not a valid hex value for defining a color'
		
	@Check
	def colorValue(StyledElement style) {
		if(style.color !== null && !style.color.isColor) {
			warning(INVALID_COLOR_MESSAGE, 
					C4DslPackage.Literals.STYLED_ELEMENT__COLOR,
					"Invalid Color Value")												
		}
		if(style.colour !== null && !style.colour.isColor) {
			warning(INVALID_COLOR_MESSAGE, 
					C4DslPackage.Literals.STYLED_ELEMENT__COLOUR,
					"Invalid Color Value")												
		}
		if(style.backgroundColor !== null && !style.backgroundColor.isColor) {
			warning(INVALID_COLOR_MESSAGE, 
					C4DslPackage.Literals.STYLED_ELEMENT__BACKGROUND_COLOR,
					"Invalid Color Value")												
		}
		if(style.stroke !== null && !style.stroke.isColor) {
			warning(INVALID_COLOR_MESSAGE, 
					C4DslPackage.Literals.STYLED_ELEMENT__STROKE,
					"Invalid Color Value")												
		}
	}

	@Check
	def colorValue(StyledRelationShip style) {
		if(style.color !== null && !style.color.isColor) {
			warning(INVALID_COLOR_MESSAGE, 
					C4DslPackage.Literals.STYLED_RELATION_SHIP__COLOR,
					"Invalid Color Value")												
		}
		if(style.colour !== null && !style.colour.isColor) {
			warning(INVALID_COLOR_MESSAGE, 
					C4DslPackage.Literals.STYLED_RELATION_SHIP__COLOUR,
					"Invalid Color Value")												
		}		
	}
	
	@Check
	def whitespacesInKey(View view) {
		if(!view.name.matches("[a-zA-Z_0-9|-]+")) {
			error('Key contains illegal characters. Must match [a-zA-Z_0-9|-]+', 
					C4DslPackage.Literals.VIEW__NAME,
					ISSUE_CODE_ILLEGAL_CHARS_IN_NAME)												
		}
	}
	
	@Check(NORMAL)
	def themeIsAvailable(Theme theme) {

		theme.urls.forEach[u, index|
			val url = new URL(u); 
			val huc = url.openConnection as HttpURLConnection;	 		
	 		if(huc.responseCode !== HttpURLConnection.HTTP_OK) {
	 			warning('URL does not exists', 
	 				C4DslPackage.Literals.THEME__URLS,
	 				index, 
	 				"No such URL"
	 			)
	 		}			
		]
	}
	
	def isColor(String colorString) {
		colorString.matches(COLOR_REGEX)
	}
	
}
