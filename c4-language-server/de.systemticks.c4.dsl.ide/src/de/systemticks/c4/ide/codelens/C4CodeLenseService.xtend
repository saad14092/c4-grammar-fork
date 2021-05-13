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
import de.systemticks.c4.c4Dsl.DynamicView
import de.systemticks.c4.c4Dsl.FilteredView
import java.io.File
import de.systemticks.c4.c4Dsl.SoftwareSystem

class C4CodeLenseService implements ICodeLensService {
	
	@Inject DocumentExtensions documentExtensions
		
	override computeCodeLenses(Document document, XtextResource resource, CodeLensParams params, CancelIndicator indicator) {
		
		val result = newArrayList

		resource.allContents.filter(View).filter[isReady].forEach[ view |			
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
					arguments = newArrayList(
						view.createFilename(resource), 
						resource.workspaceFolder, 
						resource.filename+"_workspace.enc", 
						view.name?:view.createDiagramKey
					)
				]
			]				
	}
	
	def getWorkspaceFolder(XtextResource resource) {
		resource.URI.trimSegments(1).toString()
	}
	
	def filename(Resource resource) {
		resource.URI.lastSegment.split('\\.').head + File.separator
	}
	
	// Diagram Key
	def dispatch createDiagramKey(ContainerView view) {
		view.system.label.clean+"-Container"
	}
	
	def dispatch createDiagramKey(ComponentView view) {
		(view.container.eContainer as SoftwareSystem).label.clean+'-'+view.container.label.clean+"-Component"
	}
	
	def dispatch createDiagramKey(SystemContextView view) {
		view.system.label.clean+"-SystemContext"
	}

	def dispatch createDiagramKey(SystemLandscape view) {
		"SystemLandscape"
	}

	//FIXME: 001 is hard coded, but must be determined
	def dispatch createDiagramKey(DynamicView view) {
		(view.reference.eContainer as SoftwareSystem).label.clean+view.reference.label.clean+"-Dynamic-001"
	}
	
	def dispatch createDiagramKey(DeploymentView view) {
		if(view.system !== null) {
			view.system.label.clean+"-"+view.environment.name.clean+"-Deployment"			
		}
		else {
			view.environment.name.clean+"-Deployment"						
		}
	}
	
	def dispatch createFilename(SystemLandscape view, Resource resource) {
		resource.filename+'_systemLandscape_'+".puml"
	}
	
	def dispatch createFilename(SystemContextView view, Resource resource) {		
		resource.filename+'_systemContext_'+view.system.label+".puml"		
	}	

	def dispatch createFilename(ContainerView view, Resource resource) {		
		resource.filename+'_container_'+view.system.label+".puml"		
	}	

	def dispatch createFilename(ComponentView view, Resource resource) {		
		resource.filename+'_component_'+view.container.label+".puml"		
	}	
	
	def dispatch createFilename(FilteredView view, Resource resource) {		
		resource.filename+'_filtered_'+view.baseKey+'_'+view.name+".puml"		
	}	

	def dispatch createFilename(DeploymentView view, Resource resource) {		
		resource.filename+'_deployment_'+view.createKey+".puml"		
	}	
	
	def dispatch createFilename(DynamicView view, Resource resource) {		
		resource.filename+'_dynamic_'+view.reference.label+'_'+view.name+".puml"		
	}	
	
	def dispatch isReady(View view) {
		true
	}
	
	def dispatch isReady(FilteredView view) {
		false
	}
	
	def dispatch isReady(SystemContextView view) {
		view.system !== null
	}

	def dispatch isReady(ContainerView view) {
		view.system !== null
	}

	def dispatch isReady(ComponentView view) {
		view.container !== null
	}
	
	def dispatch isReady(DeploymentView view) {
		view.system !== null || view.all
	}

	def dispatch isReady(DynamicView view) {
		view.reference !== null
	}
	
	def createKey(DeploymentView view) {
		if(view.name !== null) {
			view.name
		}
		else {
			if(view.system !== null) {
				view.system.label.clean + "-" + view.environment.name.clean + "-" + 'Deployment'				
			}
			else {
				view.environment.name.clean + "-" + 'Deployment'								
			}
		}
	}
	
	def clean(String s) {
		s.replace(' ', '')
	}
	
	
}