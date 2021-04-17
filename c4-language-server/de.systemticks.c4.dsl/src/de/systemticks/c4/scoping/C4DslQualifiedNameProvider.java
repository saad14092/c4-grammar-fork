package de.systemticks.c4.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.naming.SimpleNameProvider;

public class C4DslQualifiedNameProvider extends SimpleNameProvider {

	@Override
	public QualifiedName getFullyQualifiedName(EObject eobject) {
		
//		if(eobject instanceof StaticView) {			
//			StaticView view = (StaticView)eobject;	
//			if(view.getKey() != null) {
//		        return QualifiedName.create(view.getKey());							
//			}
//		}
		
		return super.getFullyQualifiedName(eobject);
		
    }	
	
}
