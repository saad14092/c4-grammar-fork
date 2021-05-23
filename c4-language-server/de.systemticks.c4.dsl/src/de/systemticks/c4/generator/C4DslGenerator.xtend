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
import de.systemticks.c4.c4Dsl.Workspace
import java.io.ByteArrayOutputStream
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
import java.io.FileWriter
import de.systemticks.c4.utils.C4Utils
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class C4DslGenerator extends AbstractGenerator {

	val FILE_EXTENSION_PLANTUML = '.puml'

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {

		val workspace = EcoreUtil2.getAllContentsOfType(resource.contents.get(0), Workspace)		
		
		if (workspace !== null && workspace.size === 1) {
			val views = EcoreUtil2.getAllContentsOfType(resource.contents.get(0), View)
			if (views !== null && views.size > 0) {

				val parser = new StructurizrDslParser();
				// The editor might be in dirty state, i.e. visible content in editor is not in sync with file content on disk
				// Therefore the we need to store the editor content in a temporary stream or file
				// For !include references to work, this file must be in the same directory as the source file
				val xRes = (resource as XtextResource)
				val tmp = new ByteArrayOutputStream
				val origFile = new File(xRes.URI.toFileString())
				val newFile =  new File(origFile.getParentFile(), "." + origFile.getName() + ".tmp")
				// FIXME Needs a proper exception handling
				val writer = new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8)
				try {
									
					val options = SaveOptions.defaultOptions.toOptionsMap
					options.put(XtextResource.OPTION_ENCODING, "UTF-8")
					
					xRes.doSave(tmp, options)
					writer.write(tmp.toString)
					writer.close
	
					parser.parse(newFile)				

					val outDir = determineOutputDir(resource, fsa)	
													
					generateEncodedWorkspace(parser, outDir)								
					if (C4GeneratorConfiguration.INSTANCE.getInstance().getWriterType() == C4GeneratorConfiguration.WriterType.PlantUML) {
						generatePlantUML(parser, outDir)
					} else {
						generateMermaid(parser, outDir)
					}
					
				} catch (StructurizrDslParserException e) {
					System.err.println(e.message)
				} catch (RuntimeException e) {
					System.err.println(e.message)
				} catch (IOException e) {
					System.err.println(e.message)
				} finally {
					// Close if not already closed
					writer.close
					
					if (newFile.exists) {
						newFile.delete
					}
				}
			}
			else {
				println("DSL file does not contain views. Probably imported views.")				
			}
		}
		else {
			println("DSL file does not contain a workspace, hence cannot be parsed by StructurizrParser as standalone file")
		}
	}

	def getWorkspacePath(IFileSystemAccess2 fsa) {
		fsa.getURI(".", C4DslOutputConfiguration.PLANTUML_OUTPUT).trimSegments(2)
	}

	def determineOutputDir(Resource resource, IFileSystemAccess2 fsa) {

		val ws = fsa.workspacePath
		val rs = resource.URI.toFileString.replaceAll('\\.dsl$', '')
		
		val out = new File(C4Utils.baseGenDir 
			+ File.separator 
			+ ws.lastSegment
			+ File.separator 
			+ rs.replace(ws.toFileString, '')
		)
							
		return out.absolutePath
	}

	def generateToFile(File out, String content) {
		out.parentFile.mkdirs
		val fw = new FileWriter(out)
		fw.write(content)
		fw.close
	}

	def generateEncodedWorkspace(StructurizrDslParser parser, String outDir) {
		val workspaceJson = WorkspaceUtils.toJson(parser.workspace, false)
		val encodedWorkspace = Base64.getEncoder().encodeToString(workspaceJson.getBytes());
		generateToFile(new File(outDir+File.separator+"_workspace.enc"), encodedWorkspace)		
	}

	def generatePlantUML(StructurizrDslParser parser, String outDir) {

		val writer = C4GeneratorConfiguration.INSTANCE.getInstance().getWriter()
		parser.workspace.views.views.forEach [ view |			
			generateToFile(new File(
				outDir+File.separator+view.createFileName+".puml"), 
				writer.toString(view)
			)														
		]
	}

	def generateMermaid(StructurizrDslParser parser, String outDir) {

		val writer = C4GeneratorConfiguration.INSTANCE.getInstance().getMermaidWriter()
		parser.workspace.views.views.forEach [ view |			
			generateToFile(new File(
				outDir+File.separator+view.createFileName+".mmd"), 
				writer.toString(view)
			)														
		]
	}

	def dispatch createFileName(SystemLandscapeView view) {
		'_systemLandscape_'
	}

	def dispatch createFileName(SystemContextView view) {
		'_systemContext_' + view.softwareSystem.name
	}

	def dispatch createFileName(ContainerView view) {
		'_container_' + view.softwareSystem.name
	}

	def dispatch createFileName(ComponentView view) {
		'_component_' + view.container.name
	}

	def dispatch createFileName(FilteredView view) {
		'_filtered_' + view.baseViewKey + "_" + view.key
	}

	def dispatch createFileName(DeploymentView view) {
		'_deployment_' + view.key
	}

	def dispatch createFileName(DynamicView view) {
		'_dynamic_' + view.element.name + "_" + view.key
	}

}
