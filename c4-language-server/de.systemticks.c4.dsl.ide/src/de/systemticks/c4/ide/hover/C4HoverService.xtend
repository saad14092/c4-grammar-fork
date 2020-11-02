package de.systemticks.c4.ide.hover

import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.ide.server.hover.HoverService
import de.systemticks.c4.c4Dsl.NamedElement

class C4HoverService extends HoverService {
	
	override getContents(EObject element) {
		
		val result = newArrayList
		
		if(element instanceof NamedElement) {
			
			result.add(
				'''
					**«element.label»**
					
					*Description*: «element.description»
					
					*Tags*: «element.taglist»
				'''				
				)			
		}
		
		result	  	
	}
		
}