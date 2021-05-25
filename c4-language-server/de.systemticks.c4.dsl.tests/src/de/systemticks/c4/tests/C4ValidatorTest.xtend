package de.systemticks.c4.tests

import com.google.inject.Inject
import de.systemticks.c4.c4Dsl.C4DslPackage
import de.systemticks.c4.c4Dsl.C4Model
import de.systemticks.c4.validation.C4DslValidator
import java.io.File
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse

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
			C4DslValidator.ISSUE_CODE_ILLEGAL_CHARS_IN_NAME,
			'Key contains illegal characters. Must match [a-zA-Z_0-9|-]+'
		)		
	}

	@Test
	def testViewWithSameName() {
		
		val res = C4TestHelper.loadModel(new File("./resource/dsl/invalid/view_name.dsl").absolutePath)
		val view = (res.contents.head as C4Model).workspace.viewSection.views.last

		view.assertError(
			C4DslPackage.Literals.SYSTEM_CONTEXT_VIEW,
			C4DslValidator.ISSUE_CODE_DUPLICATED_ELEMENT,
			'A View with the name'+view.name+' is already defined'
		)		
		
	}

	@Test
	def testIsColor() {	
		val validator = new C4DslValidator
		assertTrue(validator.isColor('#000000'))
		assertTrue(validator.isColor('#FFFFFF'))
		assertFalse(validator.isColor('#00000'))
		assertFalse(validator.isColor('#0000000'))
		assertFalse(validator.isColor('#00000G'))
		assertFalse(validator.isColor('#000 000'))
	}
}