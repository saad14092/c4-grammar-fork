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
import com.structurizr.dsl.StructurizrDslParserException
import com.structurizr.util.WorkspaceUtils
import com.structurizr.view.ComponentView
import com.structurizr.view.ContainerView
import com.structurizr.view.DeploymentView
import com.structurizr.view.DynamicView
import com.structurizr.view.FilteredView
import com.structurizr.view.SystemContextView
import com.structurizr.view.SystemLandscapeView
import de.systemticks.c4.c4Dsl.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Base64
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import org.eclipse.xtext.resource.SaveOptions
import org.eclipse.xtext.resource.XtextResource

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class C4DslGenerator extends AbstractGenerator {

	val FILE_EXTENSION_PLANTUML = '.puml'

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {

		val parser = new StructurizrDslParser();

		// The editor might be in dirty state, i.e. visible content in editor is not in sync with file content on disk
		// Therefore the we need to store the editor content in a temporary stream or file
		// For !include references to work, this file must be in the same directory as the source file
		val xRes = (resource as XtextResource)
		val origFile = new File(xRes.URI.toFileString())
		val newFile =  new File(origFile.getParentFile(), "." + origFile.getName() + ".tmp")

		val views = EcoreUtil2.getAllContentsOfType(resource.contents.get(0), View)
		if (views !== null && views.size > 0) {
			// FIXME Needs a proper exception handling
			val outFileStream = new FileOutputStream(newFile)
			try {
				xRes.doSave(outFileStream, SaveOptions.defaultOptions.toOptionsMap)
				outFileStream.close()

				parser.parse(newFile)				
								
				generateEncodedWorkspace(parser, resource, fsa)								
				generatePlantUML(parser, resource, fsa)
				
			} catch (StructurizrDslParserException e) {
				e.printStackTrace
			} catch (RuntimeException e) {
				e.printStackTrace
			} catch (IOException e) {
				e.printStackTrace
			} finally {
				// Close if not already closed
				outFileStream.close()
				
				if (newFile.exists()) {
					newFile.delete()
				}
			}
		}

	}

	def generateEncodedWorkspace(StructurizrDslParser parser, Resource resource, IFileSystemAccess2 fsa) {
		val workspaceJson = WorkspaceUtils.toJson(parser.workspace, false)
		val encodedWorkspace = Base64.getEncoder().encodeToString(workspaceJson.getBytes());
		val fn = resource.URI.lastSegment.split('\\.').head
		fsa.generateFile(
			fn + File.separator+"_workspace.enc", 
			C4DslOutputConfiguration.PLANTUML_OUTPUT,
			encodedWorkspace
		)		
	}

	def generatePlantUML(StructurizrDslParser parser, Resource resource, IFileSystemAccess2 fsa) {

		val writer = C4GeneratorConfiguration.INSTANCE.getInstance().getWriter()

		val fn = resource.URI.lastSegment.split('\\.').head

		parser.workspace.views.views.forEach [ view |
						
			val out = createFileName(fn + File.separator, view, FILE_EXTENSION_PLANTUML)
			fsa.generateFile(
				out,
				C4DslOutputConfiguration.PLANTUML_OUTPUT,
				writer.toString(view)
			)				
		]
	}

	def dispatch createFileName(String fn, SystemLandscapeView view, String ext) {
		fn + '_systemLandscape_' + ext
	}

	def dispatch createFileName(String fn, SystemContextView view, String ext) {
		fn + '_systemContext_' + view.softwareSystem.name + ext
	}

	def dispatch createFileName(String fn, ContainerView view, String ext) {
		fn + '_container_' + view.softwareSystem.name + ext
	}

	def dispatch createFileName(String fn, ComponentView view, String ext) {
		fn + '_component_' + view.container.name + ext
	}

	def dispatch createFileName(String fn, FilteredView view, String ext) {
		fn + '_filtered_' + view.baseViewKey + "_" + view.key + ext
	}

	def dispatch createFileName(String fn, DeploymentView view, String ext) {
		fn + '_deployment_' + view.key + ext
	}

	def dispatch createFileName(String fn, DynamicView view, String ext) {
		fn + '_dynamic_' + view.element.name + "_" + view.key + ext
	}

}
