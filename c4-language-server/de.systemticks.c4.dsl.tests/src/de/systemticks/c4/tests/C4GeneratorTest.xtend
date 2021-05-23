package de.systemticks.c4.tests

import com.structurizr.dsl.StructurizrDslParser
import de.systemticks.c4.generator.C4DslGenerator
import de.systemticks.c4.generator.C4DslOutputConfiguration
import java.io.File
import org.eclipse.xtext.generator.InMemoryFileSystemAccess
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import org.junit.Rule
import org.eclipse.xtext.xbase.testing.TemporaryFolder

@ExtendWith(InjectionExtension)
@InjectWith(C4DslInjectorProvider)
class C4GeneratorTest {

	
	@Rule
	TemporaryFolder tempFolder = new TemporaryFolder
	
	val TEST_DSL_FILES = #['amazon_web_service', 'big_bank']
	val TEST_DIR = "./resource/dsl/examples"
	
	@Test
	def void generatePuml() {

		val generator = new C4DslGenerator

		var parser = new StructurizrDslParser		
		parser.parse(new File(TEST_DIR+File.separator+TEST_DSL_FILES.get(0)+'.dsl'))
		generator.generatePlantUML(parser, tempFolder.newFolder(TEST_DSL_FILES.get(0)).absolutePath)		
		assertTrue(new File(tempFolder.newFolder(TEST_DSL_FILES.get(0)).absolutePath+File.separator+'_deployment_AmazonWebServicesDeployment.puml').exists)

		parser = new StructurizrDslParser
		parser.parse(new File(TEST_DIR+File.separator+TEST_DSL_FILES.get(1)+'.dsl'))			
		generator.generatePlantUML(parser, tempFolder.newFolder(TEST_DSL_FILES.get(1)).absolutePath)		
		assertTrue(new File(tempFolder.newFolder(TEST_DSL_FILES.get(1)).absolutePath+File.separator+'_component_API Application.puml').exists)
		assertTrue(new File(tempFolder.newFolder(TEST_DSL_FILES.get(1)).absolutePath+File.separator+'_container_Internet Banking System.puml').exists)
		assertTrue(new File(tempFolder.newFolder(TEST_DSL_FILES.get(1)).absolutePath+File.separator+'_deployment_DevelopmentDeployment.puml').exists)
		assertTrue(new File(tempFolder.newFolder(TEST_DSL_FILES.get(1)).absolutePath+File.separator+'_deployment_LiveDeployment.puml').exists)
		//assertTrue(new File(tempFolder.newFolder(TEST_DSL_FILES.get(1)).absolutePath+File.separator+'_dynamic_API Application_SignIn.puml').exists)
		assertTrue(new File(tempFolder.newFolder(TEST_DSL_FILES.get(1)).absolutePath+File.separator+'_systemContext_Internet Banking System.puml').exists)
		assertTrue(new File(tempFolder.newFolder(TEST_DSL_FILES.get(1)).absolutePath+File.separator+'_systemLandscape_.puml').exists)
				
	}

	//@Test
	def void generateEncodedWorkspace() {

		val generator = new C4DslGenerator
		val fsa = new InMemoryFileSystemAccess
		val parser = new StructurizrDslParser();
		
		val modelDir = new File(C4TestHelper.TEST_FILE_DIR)
		if(modelDir.exists) {
			modelDir.listFiles().filter[name.endsWith('dsl') && isFile].forEach[
				val res = C4TestHelper.loadModel(absolutePath)
				System.err.println('TEST '+absolutePath)
				generator.generateEncodedWorkspace(parser, generator.determineOutputDir(res, fsa))				
			]
		}

		if(modelDir.exists) {
			modelDir.listFiles().filter[name.endsWith('dsl') && isFile].forEach[				
				val fn = C4DslOutputConfiguration.PLANTUML_OUTPUT+name.split('\\.').head+File.separator+'_workspace.enc' 
				assertEquals(1, fsa.textFiles.filter[p1, p2| 
					p1.equals(fn)].size)						
			]
		}

		fsa.textFiles.forEach[p1, content| 
			assertNotNull(content)
			assertTrue(content.length > 0)
		]
				
	}
	
}