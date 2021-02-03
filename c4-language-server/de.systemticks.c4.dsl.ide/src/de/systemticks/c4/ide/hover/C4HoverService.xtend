package de.systemticks.c4.ide.hover

import de.systemticks.c4.c4Dsl.Component
import de.systemticks.c4.c4Dsl.Container
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.ide.server.hover.HoverService
import de.systemticks.c4.c4Dsl.AutoLayout
import de.systemticks.c4.c4Dsl.LayoutDirection
import de.systemticks.c4.c4Dsl.DeploymentNode
import de.systemticks.c4.c4Dsl.BasicModelElement
import de.systemticks.c4.c4Dsl.AnyModelElement
import de.systemticks.c4.c4Dsl.ContainerInstance
import de.systemticks.c4.c4Dsl.SoftwareSystemInstance
import de.systemticks.c4.c4Dsl.InfrastructureNode

class C4HoverService extends HoverService {
	
	override getContents(EObject element) {
				
		if(element instanceof AnyModelElement) {			
			return element.createHover.toString			
		}
		
		else if(element instanceof AutoLayout) {
			
			return
				'''
				**AutoLayout**
				
				*Direction*:
				&nbsp;&nbsp;&nbsp;«element.direction.layoutDirection»
				
				*Rank Separation*:
				&nbsp;&nbsp;&nbsp;«IF element.rankSeperation==0»300 px (default)«ELSE»«element.rankSeperation» px«ENDIF»
				
				*Node Separation*:
				&nbsp;&nbsp;&nbsp;«IF element.nodeSeperation==0»300 px (default)«ELSE»«element.nodeSeperation» px«ENDIF»				
				'''						
		}

		else {			
			return ""		
		}
		
	}
	
	def layoutDirection(LayoutDirection direction) {
		switch direction {
			case LayoutDirection.TB: '(tb) Top to bottom (default)'
			case LayoutDirection.BT: '(bt) Bottom to top'
			case LayoutDirection.LR: '(lr) Left to right'
			case LayoutDirection.RL: '(rl) right to left'
		}
	}
	
	private def dispatch createHover(AnyModelElement element) {
		'''
		'''
	}

	private def dispatch createHover(BasicModelElement element) {
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
	}

	private def dispatch createHover(DeploymentNode element) {
		'''
		**«element.label»**
		
		*Description*:
		 
		&nbsp;&nbsp;&nbsp;«element.description»
		
		*Technology*:
		
		&nbsp;&nbsp;&nbsp;«element.technology»
		
		*Tags*:
		 
		&nbsp;&nbsp;&nbsp;«element.taglist»
		'''
	}

	private def dispatch createHover(InfrastructureNode element) {
		'''
		**«element.label»**
		
		*Description*:
		 
		&nbsp;&nbsp;&nbsp;«element.description»
		
		*Technology*:
		
		&nbsp;&nbsp;&nbsp;«element.technology»
		
		*Tags*:
		 
		&nbsp;&nbsp;&nbsp;«element.taglist»
		'''
	}
		
	private def dispatch createHover(ContainerInstance element) {
		'''
		**«element.name»**
				
		*Container*:
		
		&nbsp;&nbsp;&nbsp;«element.container.label»
		
		*Tags*:
		 
		&nbsp;&nbsp;&nbsp;«element.taglist»
		'''
	}

	private def dispatch createHover(SoftwareSystemInstance element) {
		'''
		**«element.name»**
				
		*Software System*:
		
		&nbsp;&nbsp;&nbsp;«element.softwareSystem.label»
		
		*Tags*:
		 
		&nbsp;&nbsp;&nbsp;«element.taglist»
		'''
	}
		
}