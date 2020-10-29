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
package de.systemticks.c4.scoping

import de.systemticks.c4.c4Dsl.C4DslPackage
import de.systemticks.c4.c4Dsl.ComponentView
import de.systemticks.c4.c4Dsl.Container
import de.systemticks.c4.c4Dsl.NamedElement
import de.systemticks.c4.c4Dsl.RelationShip
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.scoping.Scopes
import de.systemticks.c4.c4Dsl.AnimationStep
import de.systemticks.c4.c4Dsl.Include
import de.systemticks.c4.c4Dsl.Exclude

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class C4DslScopeProvider extends AbstractC4DslScopeProvider {

	override IScope getScope(EObject context, EReference reference) {

		if ( context instanceof RelationShip &&
			(reference == C4DslPackage.Literals.RELATION_SHIP__FROM || reference == C4DslPackage.Literals.RELATION_SHIP__TO) ) {
			
			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, NamedElement);

			return Scopes.scopeFor(candidates);
			
		} 
		
		else if ( context instanceof ComponentView && reference == C4DslPackage.Literals.COMPONENT_VIEW__CONTAINER) {
			
			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, Container);

			return Scopes.scopeFor(candidates);
			
		}
		
		else if (context instanceof AnimationStep && reference == C4DslPackage.Literals.ANIMATION_STEP__ELEMENTS) {

			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, NamedElement);

			return Scopes.scopeFor(candidates);
			
		}

		else if (context instanceof Include && reference == C4DslPackage.Literals.INCLUDE__ELEMENTS) {

			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, NamedElement);

			return Scopes.scopeFor(candidates);
			
		}

		else if (context instanceof Exclude && reference == C4DslPackage.Literals.EXCLUDE__ELEMENTS) {

			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, NamedElement);

			return Scopes.scopeFor(candidates);
			
		}
		
		else {
			return super.getScope(context, reference);
		}

	}
	
}


