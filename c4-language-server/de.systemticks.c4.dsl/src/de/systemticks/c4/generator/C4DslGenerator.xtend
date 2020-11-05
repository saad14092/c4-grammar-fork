// Copyright (c) 2020 systemticks GmbH
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package de.systemticks.c4.generator

import com.structurizr.dsl.StructurizrDslParser
import java.io.File
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter
import com.structurizr.view.SystemLandscapeView
import com.structurizr.view.SystemContextView
import com.structurizr.view.ContainerView
import com.structurizr.view.ComponentView
import com.structurizr.view.DeploymentView

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class C4DslGenerator extends AbstractGenerator {

	val FILE_EXTENSION_PLANTUML = '.puml'

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {

		 val parser = new StructurizrDslParser();
		 val filename = resource.URI.toFileString

		 parser.parse(new File(filename));

		 generatePlantUML(parser, resource, fsa)
		 
	}
	
	def generatePlantUML(StructurizrDslParser parser, Resource resource, IFileSystemAccess2 fsa) {
				
		 val writer = new StructurizrPlantUMLWriter();
		
		 val fn = resource.URI.lastSegment.split('\\.').head

		 parser.workspace.views.views.forEach[ view |
		 	fsa.generateFile( createFileName(fn, view, FILE_EXTENSION_PLANTUML), C4DslOutputConfiguration.PLANTUML_OUTPUT, writer.toString(view))
		 ]		 				 		 		 
	}
	
	def dispatch createFileName(String fn, SystemLandscapeView view, String ext) {
		fn+'_systemLandscape_'+ext
	}

	def dispatch createFileName(String fn, SystemContextView view, String ext) {
		fn+'_systemContext_'+view.softwareSystem.name+ext
	}

	def dispatch createFileName(String fn, ContainerView view, String ext) {
		fn+'_container_'+view.softwareSystem.name+ext
	}

	def dispatch createFileName(String fn, ComponentView view, String ext) {
		fn+'_component_'+view.container.name+ext
	}

	def dispatch createFileName(String fn, DeploymentView view, String ext) {
		fn+'_deployment_'+view.softwareSystem.name+"_"+view.key+ext
	}
	
}
