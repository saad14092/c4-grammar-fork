package de.systemticks.c4.tests

import java.io.File
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import static org.junit.Assert.assertEquals

@ExtendWith(InjectionExtension)
@InjectWith(C4DslInjectorProvider)
class C4ModelsWithoutErrorTest {

	@Test
	def void modelsWithFormerIssues() {
		
		val modelDir = new File(C4TestHelper.TEST_FILE_DIR)
		if(modelDir.exists) {
			modelDir.listFiles().filter[name.startsWith('issue') && isFile].forEach[

				val res = C4TestHelper.loadModel(absolutePath)
				val issues = C4TestHelper.validate(res)
				
				assertEquals(0, issues.size)				
			]
		}		
		
	}
}