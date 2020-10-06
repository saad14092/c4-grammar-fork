package de.systemticks.c4.generator.plantuml

import de.systemticks.c4.c4Dsl.NamedElement
import de.systemticks.c4.c4Dsl.Person
import de.systemticks.c4.c4Dsl.SoftwareSystem
import de.systemticks.c4.c4Dsl.SystemContextView
import de.systemticks.c4.c4Dsl.Workspace
import java.util.List
import de.systemticks.c4.c4Dsl.Enterprise

class C4ToPlantUmlSystemContextView extends C4ToPlantUmlBaseGenerator {
		
	
	new(Workspace workspace) {
		super(workspace)
	}
	
	def transform(SystemContextView view) {
		'''
			@startuml(id=SystemContext)
			title «view.system.label» - System Context
			
			«addSkins»
			
			«val elements=view.system.allConnectedElements.filter[isNotExcluded(view)].toList»
			«val group = elements.groupBy[ eContainer instanceof Enterprise]»
			
			«IF group.get(Boolean.TRUE)?.size > 0»
			package "Big Bank plc" {
			«FOR e: group.get(Boolean.TRUE)»
				«e.transformElement»
			«ENDFOR»
			}
			«ENDIF»
			«FOR e: group.get(Boolean.FALSE)»
				«e.transformElement»
			«ENDFOR»			
			«FOR r: allRelationShips.filter[elements.contains(from) && elements.contains(to)]»
				«r.transformRelationShip»
			«ENDFOR»
			@enduml
		'''
	}
	
	def List<NamedElement> getAllConnectedElements(SoftwareSystem system) {
		
		val List<NamedElement> elems = newArrayList()
		elems.add(system)
				
		elems.addAll(allRelationShips
			.filter[ (from.equals(system) && (to instanceof Person || to instanceof SoftwareSystem)) || 
					 (to.equals(system) && (from instanceof Person || from instanceof SoftwareSystem) )
			]
			.map[from.equals(system) ? to : from].toSet
		)
					
		elems
		
	}

	def isNotExcluded(NamedElement element, SystemContextView view) {
//		if(view.exclude !== null)
//			!view.exclude.elements.contains(element)
//		else
//			true
		true
	}
}
