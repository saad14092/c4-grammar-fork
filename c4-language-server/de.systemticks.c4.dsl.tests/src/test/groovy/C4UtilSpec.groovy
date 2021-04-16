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
			tags.size() == 9
			tags.contains('Element')
			tags.contains('Person')
			tags.contains('Software System')
			tags.contains('Container')
			tags.contains('Component')
			tags.contains('Deployment Node')
			tags.contains('Infrastructure Node')
			tags.contains('Software System Instance')
			tags.contains('Container Instance')
	}

	def "Testing if custom tags can be retrieved" () {
		
		given: "A test model with some custom tags"
			def file = new File(C4TestHelper.TEST_FILE_DIR+File.separator+"c4-dsl-extension.dsl").absolutePath
			def res = C4TestHelper.loadModel(file)
			def workspace = (res.contents.get(0) as C4Model).getWorkspace()

		when:
			def tags = C4Utils.allTags(workspace)
			
		then:
			tags.size() == 14
			tags.contains('Structurizr')
			tags.contains('BuiltIn')
			tags.contains('Extern')
			tags.contains('Extension')
			tags.contains('File')
	}
	
}
