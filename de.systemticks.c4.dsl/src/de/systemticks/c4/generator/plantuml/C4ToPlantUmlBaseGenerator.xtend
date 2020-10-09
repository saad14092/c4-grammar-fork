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

import static extension de.systemticks.c4.utils.C4Utils.*
import de.systemticks.c4.c4Dsl.StyledRelationShip

class C4ToPlantUmlBaseGenerator {
	
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

	def addCustomStyles()
	{
		'''
			' all custom styles
			«FOR style: workspace.allStyledElements»
				«style.createCustomStyle?.toSkinParam»
			«ENDFOR»
		'''
	}
	
	def findStyle(NamedElement e) {	
		e.tags.map[ tag | workspace.allStyledElements.findFirst[ s | s.tag.equals(tag)]].head		
	}
				
	def dispatch transformElement(NamedElement e) {
		'''
			todo for «e.name»
		'''
	}
		
	def dispatch transformElement(Person p) {
		'''
			«p.findStyle?.shape.toSkinType?:DEFAULT_SHAPE» "==«p.label»\n<size:10>[«DEFAULT_PERSON_TAG»]</size>\n«p.addDescription»" <<«p.findStyle?.tag?:DEFAULT_PERSON_TAG»>> as «p.name»
		'''
	}

	def dispatch transformElement(SoftwareSystem s) {
		'''
			«s.findStyle?.shape.toSkinType?:DEFAULT_SHAPE» "==«s.label»\n<size:10>[«DEFAULT_SOFTWARE_SYSTEM_TAG»]</size>\n«s.addDescription»" <<«s.findStyle?.tag?:DEFAULT_SOFTWARE_SYSTEM_TAG»>> as «s.name»
		'''
	}

	def dispatch transformElement(Container c) {
		'''
			«c.findStyle?.shape.toSkinType?:DEFAULT_SHAPE» "==«c.label»\n<size:10>[«DEFAULT_CONTAINER_TAG»]</size>\n«c.addDescription»" <<«c.findStyle?.tag?:DEFAULT_CONTAINER_TAG»>> as «c.name»
		'''
	}

	def dispatch transformElement(Component c) {
		'''
			«c.findStyle?.shape.toSkinType?:DEFAULT_SHAPE» "==«c.label»\n<size:10>[«DEFAULT_COMPONENT_TAG»]</size>\n«c.addDescription»" <<«c.findStyle?.tag?:DEFAULT_COMPONENT_TAG»>> as «c.name»
		'''
	}

	def transformRelationShip(RelationShip r) {
		'''
			«r.from.name» «r.transformStyle.toArrow» «r.to.name» : "«r.description»"«IF r.technology!==null»\n<size:8>[«r.technology»]</size>«ENDIF»
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
			«addBuiltInStyles»
			«addCustomStyles»
		'''
	}
		
	private def overrideBuiltInStyle(StyledElement baseStyle) {			
		workspace.allStyledElements.findFirst[tag.equals(baseStyle.tag)]?.overrideStyle(baseStyle)?:baseStyle
	}
	
	private def overrideStyle(StyledElement derivedStyle, StyledElement baseStyle) {
		baseStyle => [
			tag = derivedStyle.tag
			if(!derivedStyle.shape?.equals(StyleShape.BOX)) shape = derivedStyle.shape
			if(derivedStyle.backgroundColor !== null) backgroundColor = derivedStyle.backgroundColor
			if(derivedStyle.color !== null) color = derivedStyle.color
		]					
	}
	
	private def createCustomStyle(StyledElement custom) {
		
		val base = custom.baseStyleElement?.overrideBuiltInStyle
		if(base !== null) {			
			custom.overrideStyle(base)			
		}
		else {
			null
		}
	}
	
	private def getBaseStyleElement(StyledElement custom) {
		switch workspace.allNamedElements.findFirst[customTags.contains(custom.tag)] {
			Person : createDefaultStylePerson
			SoftwareSystem : createDefaultStyleSoftwareSystem
			Container : createDefaultStyleContainer
			Component : createDefaultStyleComponent
		}
	}
	
	def addBuiltInStyles() {
		'''
			' all built-in styles
			«createDefaultStylePerson.overrideBuiltInStyle.toSkinParam»
			«createDefaultStyleSoftwareSystem.overrideBuiltInStyle.toSkinParam»
			«createDefaultStyleContainer.overrideBuiltInStyle.toSkinParam»
			«createDefaultStyleComponent.overrideBuiltInStyle.toSkinParam»
		'''
	}
	
	def toSkinParam(StyledElement style) {
		'''
			skinparam «style.shape.toSkinType»<<«style.tag»>> {
			  «IF style.backgroundColor !== null»BackgroundColor «style.backgroundColor»«ENDIF»
			  «IF style.color !== null»FontColor «style.color»«ENDIF»
			  «IF style.shape?.equals(StyleShape.ROUNDED_BOX)»roundcorner 20«ENDIF»
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
	
	private def transformStyle(RelationShip r) {
		
		val style = createDefaultStyleRelationship
		val allStyles = workspace.allStyledRelationships
		
		if(allStyles.size > 0) {
			r.customTags.forEach[ t |
//				val derived = allStyles.findFirst[tag.equals(t)]
//				if(derived !== null) {
//					println("--> "+t+", "+derived+", "+style)
//					overrideStyle(derived, style)
//					println("<-- "+style)
//				}
				allStyles.findFirst[tag.equals(t)]?.overrideStyle(style)
			]				
		}
		
		style
	}
	
	private def overrideStyle(StyledRelationShip derivedStyle, StyledRelationShip baseStyle) {
		baseStyle => [
			if(derivedStyle.color !== null) color = derivedStyle.color
			if(derivedStyle.dashed !== null)  dashed = derivedStyle.dashed
		]					
	}
	
	
	private def toArrow(StyledRelationShip rStyle) {
		if(rStyle.isDashed) {
			'.['+rStyle.color+'].>'
		}
		else {
			'-['+rStyle.color+']->'			
		}
	}
}
