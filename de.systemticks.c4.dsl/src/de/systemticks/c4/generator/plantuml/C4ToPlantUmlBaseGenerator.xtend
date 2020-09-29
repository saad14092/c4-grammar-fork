package de.systemticks.c4.generator.plantuml

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

import static extension de.systemticks.c4.utils.C4Utils.*

class C4ToPlantUmlBaseGenerator {
	
	public final String DEFAULT_CONTAINER_TAG = "Container"
	public final String DEFAULT_PERSON_TAG = "Person"
	public final String DEFAULT_SOFTWARE_SYSTEM_TAG = "Software System"
	public final String DEFAULT_COMPONENT_TAG = "Component"
	public final String DEFAULT_SHAPE = "rectangle"
	
	final Workspace workspace
	
	new (Workspace _workspace) {
		workspace = _workspace
	}
	
	def getSoftwareSystem(Container c) {
		c.eContainer as SoftwareSystem
	}
	
	def allRelationShips() {
	    workspace.allRelationShips;		
	}
//
//	def allStyles() {
//	    return EcoreUtil2.getAllContentsOfType(workspace, StyledElement);		
//	}
//
//	def allNamedElements() {
//	    return EcoreUtil2.getAllContentsOfType(workspace, NamedElement);		
//	}
//
//	def List<String> getTags(NamedElement e) {
//		if(e.taglist === null) {
//			newArrayList
//		}
//		else {
//			e.taglist.split(',')			
//		}
//	}

	def addCustomStyles()
	{
		'''
			«FOR style: workspace.allStyles»
				«style.createCustomStyle?.toSkinParam»
			«ENDFOR»
		'''
	}
	
	def getApplicableStyle(NamedElement e) {
		workspace.allStyles.filter[ e.tags.contains( tag ) ].head
	}
	
	def getShape(NamedElement e) 
	{
		e.applicableStyle?.toSkinParam?:DEFAULT_SHAPE
	}
				
	def dispatch transformElement(NamedElement e) {
		'''
			todo for «e.name»
		'''
	}
		
	def dispatch transformElement(Person p) {
		'''
			«p.applicableStyle?.shape.toSkinType?:DEFAULT_SHAPE» "==«p.label»\n<size:10>[«DEFAULT_PERSON_TAG»]</size>\n«p.addDescription»" <<«p.applicableStyle?.tag?:DEFAULT_PERSON_TAG»>> as «p.name»
		'''
	}

	def dispatch transformElement(SoftwareSystem s) {
		'''
			«s.applicableStyle?.shape.toSkinType?:DEFAULT_SHAPE» "==«s.label»\n<size:10>[«DEFAULT_SOFTWARE_SYSTEM_TAG»]</size>\n«s.addDescription»" <<«s.applicableStyle?.tag?:DEFAULT_SOFTWARE_SYSTEM_TAG»>> as «s.name»
		'''
	}

	def dispatch transformElement(Container c) {
		'''
			«c.applicableStyle?.shape.toSkinType?:DEFAULT_SHAPE» "==«c.label»\n<size:10>[«DEFAULT_CONTAINER_TAG»]</size>\n«c.addDescription»" <<«c.applicableStyle?.tag?:DEFAULT_CONTAINER_TAG»>> as «c.name»
		'''
	}

	def dispatch transformElement(Component c) {
		'''
			«c.applicableStyle?.shape.toSkinType?:DEFAULT_SHAPE» "==«c.label»\n<size:10>[«DEFAULT_COMPONENT_TAG»]</size>\n«c.addDescription»" <<«c.applicableStyle?.tag?:DEFAULT_COMPONENT_TAG»>> as «c.name»
		'''
	}

	def transformRelationShip(RelationShip r) {
		'''
			«r.from.name» .[#707070].> «r.to.name» : "«r.description»"«IF r.technology!==null»\n<size:8>[«r.technology»]</size>«ENDIF»
		'''
	}
	
	def addDescription(NamedElement e) {
		if(e.description!==null && e.description.length>0) {
			'\\n'+e.description
		}
		else ''
	}
	
	def addSkins() 
	{
		'''
			«defaultSkinGeneral»
			«addBaseStyles»
			«addCustomStyles»
		'''
	}
	
	private def createDefaultStyleContainer() {
		C4DslFactory.eINSTANCE.createStyledElement => [
			backgroundColor = "#438dd5"
			color = "#ffffff"
			shape = StyleShape.BOX		
			tag = DEFAULT_CONTAINER_TAG					
		]
	}

	private def createDefaultStyleSoftwareSystem() {
		C4DslFactory.eINSTANCE.createStyledElement => [
			backgroundColor = "#1168bd"
			color = "#ffffff"
			shape = StyleShape.BOX
			tag = DEFAULT_SOFTWARE_SYSTEM_TAG			
		]
	}

	private def createDefaultStylePerson() {
		C4DslFactory.eINSTANCE.createStyledElement => [
			backgroundColor = "#08427b"
			color = "#ffffff"
			shape = StyleShape.BOX	
			tag = DEFAULT_PERSON_TAG								
		]
	}

	private def createDefaultStyleComponent() {
		C4DslFactory.eINSTANCE.createStyledElement => [
			backgroundColor = "#85bbf0"
			color = "#000000"
			shape = StyleShape.BOX
			tag = DEFAULT_COMPONENT_TAG						
		]
	}
	
	private def createCustomStyle(StyledElement custom) {
		
		val base = custom.baseStyleElement
		if(base !== null) {
			base => [
				tag = custom.tag
				if(custom.shape !== null) shape = custom.shape
				if(custom.backgroundColor !== null) backgroundColor = custom.backgroundColor
				if(custom.color !== null) color = custom.color
			]			
		}
		else {
			null
		}
	}
	
	private def getBaseStyleElement(StyledElement custom) {
		switch workspace.allNamedElements.findFirst[tags.contains(custom.tag)] {
			Person : createDefaultStylePerson
			SoftwareSystem : createDefaultStyleSoftwareSystem
			Container : createDefaultStyleContainer
			Component : createDefaultStyleComponent
		}
	}
	
	def addBaseStyles() {
		'''
			«createDefaultStylePerson.toSkinParam»
			«createDefaultStyleSoftwareSystem.toSkinParam»
			«createDefaultStyleContainer.toSkinParam»
			«createDefaultStyleComponent.toSkinParam»
		'''
	}
	
	def toSkinParam(StyledElement style) {
		'''
			skinparam «style.shape.toSkinType»<<«style.tag»>> {
			  «IF style.backgroundColor !== null»BackgroundColor «style.backgroundColor»«ENDIF»
			  «IF style.color !== null»FontColor «style.color»«ENDIF»
			}
		'''
	}	
	
	private def toSkinType(StyleShape shape) {
		switch shape {
			case CYLINDER : 'database'
			case PERSON : 'actor'
			default: DEFAULT_SHAPE
		}
	}
	
	private def defaultSkinGeneral() {
		'''
			skinparam {
			  shadowing false
			  arrowFontSize 10
			  defaultTextAlignment center
			  wrapWidth 200
			  maxMessageSize 100
			}
			hide stereotype
			top to bottom direction
		'''
	}
	
	
}
