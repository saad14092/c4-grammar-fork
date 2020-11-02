package de.systemticks.c4.ide.hover

import de.systemticks.c4.c4Dsl.Component
import de.systemticks.c4.c4Dsl.Container
import de.systemticks.c4.c4Dsl.NamedElement
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.ide.server.hover.HoverService
import de.systemticks.c4.c4Dsl.AutoLayout
import de.systemticks.c4.c4Dsl.LayoutDirection

class C4HoverService extends HoverService {
	
	override getContents(EObject element) {
		
		val result = newArrayList
		
		if(element instanceof NamedElement) {
			
			result.add(
				'''
					**«element.label»**
					
					*Description*:
					 
					&nbsp;&nbsp;&nbsp;«element.description»
					
					«IF element instanceof Container»
					*Technology*:
					
					&nbsp;&nbsp;&nbsp;«element.technology»
					«ENDIF»
					«IF element instanceof Component»
					*Technology*:
					
					&nbsp;&nbsp;&nbsp;«element.technology»
					«ENDIF»
					
					*Tags*:
					 
					&nbsp;&nbsp;&nbsp;«element.taglist»
				'''				
				)			
		}
		
		else if(element instanceof AutoLayout) {
			
			result.add(
				'''
				**AutoLayout**
				
				*Direction*:
				&nbsp;&nbsp;&nbsp;«element.direction.layoutDirection»
				
				*Rank Separation*:
				&nbsp;&nbsp;&nbsp;«IF element.rankSeperation==0»300 px (default)«ELSE»«element.rankSeperation» px«ENDIF»
				
				*Node Separation*:
				&nbsp;&nbsp;&nbsp;«IF element.nodeSeperation==0»300 px (default)«ELSE»«element.nodeSeperation» px«ENDIF»				
				'''
			)
			
		}
		
		result	  	
	}
	
	def layoutDirection(LayoutDirection direction) {
		switch direction {
			case LayoutDirection.TB: '(tb) Top to bottom (default)'
			case LayoutDirection.BT: '(bt) Bottom to top'
			case LayoutDirection.LR: '(lr) Left to right'
			case LayoutDirection.RL: '(rl) right to left'
		}
	}
		
}