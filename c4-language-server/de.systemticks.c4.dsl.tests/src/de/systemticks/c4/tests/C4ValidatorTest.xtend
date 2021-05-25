package de.systemticks.c4.tests

import com.google.inject.Inject
import de.systemticks.c4.c4Dsl.C4DslPackage
import de.systemticks.c4.c4Dsl.C4Model
import java.io.File
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

@ExtendWith(InjectionExtension)
@InjectWith(C4DslInjectorProvider)
class C4ValidatorTest {

	
	@Inject
	extension ValidationTestHelper
	
	@Test
	def testWhitespacesInKey() {
		val res = C4TestHelper.loadModel(new File("./resource/dsl/invalid/view_name.dsl").absolutePath)
		val view = (res.contents.head as C4Model).workspace.viewSection.views.head
		
		view.assertError(
			C4DslPackage.Literals.SYSTEM_LANDSCAPE,
			"Key contains whitespaces",
			"Key contains illegal characters. Must match [a-zA-Z_0-9|-]+"
		)		
	}

	//TODO 
	// This is a first example for testing the custom validation rules. 
	// All other custom validation rules should be tested here	
}