package de.systemticks.c4.generator.plantuml

import de.systemticks.c4.c4Dsl.Container
import de.systemticks.c4.c4Dsl.NamedElement
import java.util.List

import de.systemticks.c4.c4Dsl.ComponentView
import de.systemticks.c4.c4Dsl.Workspace

class C4ToPlantUmlComponentView extends C4ToPlantUmlBaseGenerator {

	
	new(Workspace workspace) {
		super(workspace)
	}
	
	def transform(ComponentView view) {
		val allElements=(view.container.connectedElements + view.container.directElements).toList 
		'''
			@startuml(id=Components)
			title «view.container.softwareSystem.label» - «view.container.label» - Components
			
			«addSkins»
			
			«FOR connected: view.container.connectedElements»
				«connected.transformElement»
			«ENDFOR»
			package "«view.container.label»\n[«DEFAULT_CONTAINER_TAG»]" {
			«FOR direct: view.container.directElements»
				«direct.transformElement»
			«ENDFOR»
			}
			«FOR r: allRelationShips
				.filter[allElements.contains(from) && allElements.contains(to)]
				.map[transformRelationShip.toString].toSet»
				«r»
			«ENDFOR»
			@enduml
			
		'''
	}
	
	def List<NamedElement> getDirectElements(Container container) {
		container.components.map[it as NamedElement].toList 
	}	
	
	def List<NamedElement> getConnectedElements(Container container) {
				
		val List<NamedElement> elems = newArrayList()
//		elems.addAll(system.container)

	    val relationShips = allRelationShips
		
		container.components.forEach[ c | 
			relationShips.forEach[ r | 
				if(r.from.equals(c) || r.to.equals(c)) {
					r.from.equals(c) ? elems.add(r.to) : elems.add(r.from)
				} 
			]			
		]
		
		elems.toSet.filter[ !container.directElements.contains(it)].toList		
		
	}
					
}
