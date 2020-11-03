package de.systemticks.c4.ide.codelens

import com.google.inject.Inject
import de.systemticks.c4.c4Dsl.ComponentView
import de.systemticks.c4.c4Dsl.ContainerView
import de.systemticks.c4.c4Dsl.StyledElement
import de.systemticks.c4.c4Dsl.SystemContextView
import de.systemticks.c4.c4Dsl.SystemLandscape
import de.systemticks.c4.c4Dsl.View
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.lsp4j.CodeLens
import org.eclipse.lsp4j.CodeLensParams
import org.eclipse.lsp4j.Command
import org.eclipse.xtext.ide.server.Document
import org.eclipse.xtext.ide.server.DocumentExtensions
import org.eclipse.xtext.ide.server.codelens.ICodeLensService
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.util.CancelIndicator
import de.systemticks.c4.c4Dsl.BasicModelElement
import de.systemticks.c4.c4Dsl.DeploymentView
import de.systemticks.c4.c4Dsl.AnyModelElement
import de.systemticks.c4.c4Dsl.InfrastructureNode
import de.systemticks.c4.c4Dsl.DeploymentNode
import de.systemticks.c4.c4Dsl.ContainerInstance
import de.systemticks.c4.c4Dsl.SoftwareSystemInstance

class C4CodeLenseService implements ICodeLensService {
	
	@Inject DocumentExtensions documentExtensions
		
	override computeCodeLenses(Document document, XtextResource resource, CodeLensParams params, CancelIndicator indicator) {
		
		val result = newArrayList

		resource.allContents.filter(View).forEach[ view |			
		    result += view.createCodeLensForPlantUML(resource)						
		]

		resource.allContents.filter(StyledElement).forEach[ style |
			val styleTag = style.tag
			
			resource.allContents.filter(AnyModelElement).forEach[ element |
				if(element.taglist !== null && element.taglist.contains(styleTag)) {
					val range = documentExtensions.newRange(resource, NodeModelUtils.findActualNodeFor(style).textRegion)								
					result += new CodeLens => [
						it.range = range
						command = new Command => [
							title = element.makeTitle
							command = "c4.goto.taggedElement"
							arguments = newArrayList(documentExtensions.newRange(resource, NodeModelUtils.findActualNodeFor(element).textRegion))
						]
					]									
				}
			]						
		]

		return result
	}
	
	def dispatch makeTitle(BasicModelElement element) {
		element.label
	}
	
	def dispatch makeTitle(InfrastructureNode element) {
		element.label
	}

	def dispatch makeTitle(DeploymentNode element) {
		element.label
	}
	
	def dispatch makeTitle(ContainerInstance element) {
		element.name
	}

	def dispatch makeTitle(SoftwareSystemInstance element) {
		element.name
	}
		
	def createCodeLensForPlantUML(View view, XtextResource resource) {
		
		    val range = documentExtensions.newRange(resource, NodeModelUtils.findActualNodeFor(view).textRegion)			
			new CodeLens => [
				it.range = range
				command = new Command => [
					title = "$(link-external) Show as PlantUML"
					command = "c4.show.diagram"
					arguments = newArrayList(view.createFilename(resource))
				]
			]				
	}
	
	def dispatch createFilename(SystemLandscape view, Resource resource) {
		val fn = resource.URI.lastSegment.split('\\.').head		
		fn+'_systemLandscape_'+".puml"
	}
	
	def dispatch createFilename(SystemContextView view, Resource resource) {		
		val fn = resource.URI.lastSegment.split('\\.').head		
		fn+'_systemContext_'+view.system.label+".puml"		
	}	

	def dispatch createFilename(ContainerView view, Resource resource) {		
		val fn = resource.URI.lastSegment.split('\\.').head		
		fn+'_container_'+view.system.label+".puml"		
	}	

	def dispatch createFilename(ComponentView view, Resource resource) {		
		val fn = resource.URI.lastSegment.split('\\.').head		
		fn+'_component_'+view.container.label+".puml"		
	}	
	
	def dispatch createFilename(DeploymentView view, Resource resource) {		
		val fn = resource.URI.lastSegment.split('\\.').head		
		fn+'_deployment_'+view.system.label+'_'+view.key+".puml"		
	}	
	
	
}