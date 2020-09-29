package de.systemticks.c4.generator.plantuml

import de.systemticks.c4.c4Dsl.Component
import de.systemticks.c4.c4Dsl.ContainerView
import de.systemticks.c4.c4Dsl.NamedElement
import de.systemticks.c4.c4Dsl.RelationShip
import de.systemticks.c4.c4Dsl.SoftwareSystem
import java.util.List

import de.systemticks.c4.c4Dsl.Container
import de.systemticks.c4.c4Dsl.Workspace

/* Container view: Include all containers within the software system in scope; 
 * plus all people and software systems that are directly connected to those containers.*/

class C4ToPlantUmlContainerView extends C4ToPlantUmlBaseGenerator {
	
	
	new(Workspace workspace) {
		super(workspace)
	}
	
	def transform(ContainerView view) {
		val allElements=(view.system.connectedElements + view.system.directElements).toList
		'''
			@startuml(id=Containers)
			title «view.system.label» - Containers
			
			«addSkins»
			
			«FOR connected: view.system.connectedElements»
				«connected.transformElement»
			«ENDFOR»
			package "«view.system.label»\n[«DEFAULT_SOFTWARE_SYSTEM_TAG»]" {
			«FOR direct: view.system.directElements»
				«direct.transformElement»
			«ENDFOR»		
			}
			«FOR r: allRelationShips
				.filter[ (allElements.contains(from) || allElements.contains(from.eContainer)) && (allElements.contains(to) || allElements.contains(to.eContainer))]
				.map[transformRelationShip.toString].toSet»
				«r»
			«ENDFOR»
			@enduml
			
		'''
	}
	
	override dispatch transformElement(Component cmp) {
		val c = (cmp.eContainer as Container)
		'''
			rectangle "==«c.label»\n<size:10>[«DEFAULT_CONTAINER_TAG»]</size>\n«c.addDescription»" <<«DEFAULT_CONTAINER_TAG»>> as «c.name»
		'''
	}
	
	override transformRelationShip(RelationShip r) {
		val from = (r.from instanceof Component) ? (r.from.eContainer as Container).name : r.from.name
		val to = (r.to instanceof Component) ? (r.to.eContainer as Container).name : r.to.name			
		'''
			«from» .[#707070].> «to» : "«r.description»"
		'''
	}
	
	
	def List<NamedElement> getDirectElements(SoftwareSystem system) {
		system.container.map[it as NamedElement].toList 
	}
	
	def List<NamedElement> getConnectedElements(SoftwareSystem system) {
		
		val List<NamedElement> elems = newArrayList()

	    val relationShips = allRelationShips
		
		system.container.forEach[ c |
			relationShips.forEach[ r | 
				if(r.from.equals(c) || r.to.equals(c)) {
					r.from.equals(c) ? elems.add(r.to) : elems.add(r.from)
				} 
			]
			c.components.forEach[ cmp | 
				relationShips.forEach[ r | 
					if(r.from.equals(cmp) || r.to.equals(cmp)) {
						r.from.equals(cmp) ? elems.add(r.to) : elems.add(r.from)
					} 
				]				
			]
		]
																		
		elems.filter[! (system.directElements.contains(it) || system.directElements.contains(it.eContainer)) ].toSet.toList		
		
	}
	
}
