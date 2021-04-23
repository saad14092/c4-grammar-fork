package test.groovy

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

import de.systemticks.c4.c4Dsl.C4Model
import de.systemticks.c4.tests.C4TestHelper
import de.systemticks.c4.utils.C4Utils
import spock.lang.Specification

class C4UtilSpec extends Specification {

	def "Testing if all default tags can be retrieved" () {
		
		given: "A test model with only default tags"
			def file = new File(C4TestHelper.TEST_FILE_DIR+File.separator+"test.dsl").absolutePath
			def res = C4TestHelper.loadModel(file)
			def workspace = (res.contents.get(0) as C4Model).getWorkspace()

		when:
			def tags = C4Utils.allTags(workspace)
			
		then:
			with(tags) {
				size() == 9
				contains('Element')
				contains('Person')
				contains('Software System')
				contains('Container')
				contains('Component')
				contains('Deployment Node')
				contains('Infrastructure Node')
				contains('Software System Instance')
				contains('Container Instance')
			}
	}

	def "Testing if custom tags can be retrieved" () {
		
		given: "A test model with some custom tags"
			def file = new File(C4TestHelper.TEST_FILE_DIR+File.separator+"c4-dsl-extension.dsl").absolutePath
			def res = C4TestHelper.loadModel(file)
			def workspace = (res.contents.get(0) as C4Model).getWorkspace()

		when:
			def tags = C4Utils.allTags(workspace)
			
		then:
			with(tags) {
				size() == 14
				contains('Structurizr')
				contains('BuiltIn')
				contains('Extern')
				contains('Extension')
				contains('File')
			}
	}
	
}
