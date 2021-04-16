package de.systemticks.c4.tests

import de.systemticks.c4.C4DslStandaloneSetup
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.util.CancelIndicator
import org.eclipse.xtext.validation.CheckMode

class C4TestHelper {

	static public String TEST_FILE_DIR = "../../workspace"
		
	static def loadModel(String file) {
		
		val injector = new C4DslStandaloneSetup().createInjectorAndDoEMFRegistration();
		
		val resourceSet = injector.getInstance(XtextResourceSet);
		
		val resource = resourceSet.getResource(URI.createFileURI(file), true);		

		resource		
	}
	
	static def validate(Resource r) {
		val validator = (r as XtextResource).getResourceServiceProvider().getResourceValidator();
		return validator.validate(r, CheckMode.ALL, CancelIndicator.NullImpl);		
	}
		
}