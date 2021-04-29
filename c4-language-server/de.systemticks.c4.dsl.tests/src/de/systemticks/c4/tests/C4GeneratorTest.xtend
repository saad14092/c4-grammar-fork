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

@ExtendWith(InjectionExtension)
@InjectWith(C4DslInjectorProvider)
class C4GeneratorTest {

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
				generator.generateEncodedWorkspace(parser, res, fsa)				
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