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

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class C4DslGenerator extends AbstractGenerator {

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {

		generateWithStructurizrDslParser(resource, fsa)					
	}
	
	def generateWithStructurizrDslParser(Resource resource, IFileSystemAccess2 fsa) {
				
		 val parser = new StructurizrDslParser();
		 val filename = resource.URI.toFileString

		 parser.parse(new File(filename));

		 val writer = new StructurizrPlantUMLWriter();
		
		 val fn = resource.URI.lastSegment.split('\\.').head

		 // generate all system context views				
		 parser.workspace.views.systemLandscapeViews.forEach[ landscapeView | 
		 	fsa.generateFile(fn+'_systemLandscape_'+".puml", writer.toString(landscapeView))
		 ]
		  
		 parser.workspace.views.systemContextViews.forEach[ contextView | 
		 	fsa.generateFile(fn+'_systemContext_'+contextView.softwareSystem.name+".puml", writer.toString(contextView))
		 ]
		 
		 parser.workspace.views.containerViews.forEach[ containerView |
		 	fsa.generateFile(fn+'_container_'+containerView.softwareSystem.name+".puml", writer.toString(containerView))		 	
		 ]

		 parser.workspace.views.componentViews.forEach[ componentView |
		 	fsa.generateFile(fn+'_component_'+componentView.container.name+".puml", writer.toString(componentView))		 	
		 ]
				 		 		 
	}
}
