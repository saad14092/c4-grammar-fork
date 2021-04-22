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

import de.systemticks.c4.c4Dsl.AnimationStep
import de.systemticks.c4.c4Dsl.BasicModelElement
import de.systemticks.c4.c4Dsl.C4DslPackage
import de.systemticks.c4.c4Dsl.ComponentView
import de.systemticks.c4.c4Dsl.Container
import de.systemticks.c4.c4Dsl.ContainerInstance
import de.systemticks.c4.c4Dsl.DeploymentElement
import de.systemticks.c4.c4Dsl.DeploymentEnvironment
import de.systemticks.c4.c4Dsl.DeploymentView
import de.systemticks.c4.c4Dsl.DynamicView
import de.systemticks.c4.c4Dsl.Exclude
import de.systemticks.c4.c4Dsl.FilteredRelationShip
import de.systemticks.c4.c4Dsl.FilteredView
import de.systemticks.c4.c4Dsl.Include
import de.systemticks.c4.c4Dsl.Model
import de.systemticks.c4.c4Dsl.RelationShip
import de.systemticks.c4.c4Dsl.SoftwareSystem
import de.systemticks.c4.c4Dsl.SoftwareSystemInstance
import de.systemticks.c4.c4Dsl.StaticView
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.scoping.Scopes
import org.eclipse.xtext.scoping.impl.FilteringScope
import de.systemticks.c4.c4Dsl.Import
import java.util.List
import de.systemticks.c4.c4Dsl.SystemContextView

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class C4DslScopeProvider extends AbstractC4DslScopeProvider {

	private def includedViaImports(List<Import> _imports, IEObjectDescription eobjd) {
		_imports.map[importURI].filter[i|
			val fs = eobjd.EObjectURI.toFileString.replace('\\', '/')
			fs.endsWith(i.replace('"',''))
		].size > 0		
	}

	override IScope getScope(EObject context, EReference reference) {


		if ( context instanceof RelationShip &&
			(reference == C4DslPackage.Literals.RELATION_SHIP__FROM || reference == C4DslPackage.Literals.RELATION_SHIP__TO) ) {
			
			val rootElement = EcoreUtil2.getRootContainer(context);
			val localBasicElements = EcoreUtil2.getAllContentsOfType(rootElement, BasicModelElement)				
			
			switch context.eContainer {
				DeploymentEnvironment : return Scopes.scopeFor(EcoreUtil2.getAllContentsOfType(rootElement, DeploymentElement))
				Model: return new FilteringScope(super.getScope(context, reference), 
					[e | (context.eContainer as Model).includes.includedViaImports(e) || localBasicElements.contains(e.EObjectOrProxy) ])
				SoftwareSystem: return new FilteringScope(super.getScope(context, reference), 
					[e | (context.eContainer as SoftwareSystem).includes.includedViaImports(e) || localBasicElements.contains(e.EObjectOrProxy) ])
				default: return Scopes.scopeFor(localBasicElements) 	
			}
						
		} 

		else if ( context instanceof SystemContextView && reference == C4DslPackage.Literals.SYSTEM_CONTEXT_VIEW__SYSTEM) {
			
			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, SoftwareSystem);
			val model = EcoreUtil2.getAllContentsOfType(rootElement, Model).head;
			
			return new FilteringScope(super.getScope(context, reference), 
					[e | model?.includes.includedViaImports(e) || candidates.contains(e.EObjectOrProxy) ])
			
		}
		
		else if ( context instanceof ComponentView && reference == C4DslPackage.Literals.COMPONENT_VIEW__CONTAINER) {
			
			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, Container);

			return Scopes.scopeFor(candidates);
			
		}
		
		else if( context instanceof DynamicView && reference ==  C4DslPackage.Literals.DYNAMIC_VIEW__REFERENCE) {

			val rootElement = EcoreUtil2.getRootContainer(context);
			val container = EcoreUtil2.getAllContentsOfType(rootElement, Container);
			val systems = EcoreUtil2.getAllContentsOfType(rootElement, SoftwareSystem);

			return Scopes.scopeFor(container + systems);
			
		}
		
		else if(context instanceof DeploymentView && reference == C4DslPackage.Literals.DEPLOYMENT_VIEW__ENVIRONMENT) {
			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, DeploymentEnvironment);			
			return Scopes.scopeFor(candidates);			
		}
		
		else if (context instanceof AnimationStep && reference == C4DslPackage.Literals.ANIMATION_STEP__ELEMENTS) {

			val rootElement = EcoreUtil2.getRootContainer(context);
			if(context.eContainer.eContainer instanceof DeploymentView) {
				return Scopes.scopeFor(EcoreUtil2.getAllContentsOfType(rootElement, DeploymentElement));				
			}
			else {
				return Scopes.scopeFor(EcoreUtil2.getAllContentsOfType(rootElement, BasicModelElement));								
			}
			
		}

		else if (context instanceof Include && reference == C4DslPackage.Literals.INCLUDE__ELEMENTS) {

			val rootElement = EcoreUtil2.getRootContainer(context);
			if(context.eContainer.eContainer instanceof DeploymentView) {
				return Scopes.scopeFor(EcoreUtil2.getAllContentsOfType(rootElement, DeploymentElement));				
			}
			else {
				val model = EcoreUtil2.getAllContentsOfType(rootElement, Model).head;
				val localBasicElements = EcoreUtil2.getAllContentsOfType(rootElement, BasicModelElement)
				
				return new FilteringScope(super.getScope(context, reference), 
						[e | model?.includes.includedViaImports(e) || localBasicElements.contains(e.EObjectOrProxy) ])
			}
			
		}

		else if (context instanceof Exclude && reference == C4DslPackage.Literals.EXCLUDE__ELEMENTS) {

			val rootElement = EcoreUtil2.getRootContainer(context);
			if(context.eContainer.eContainer instanceof DeploymentView) {
				return Scopes.scopeFor(EcoreUtil2.getAllContentsOfType(rootElement, DeploymentElement));				
			}
			else {
				val model = EcoreUtil2.getAllContentsOfType(rootElement, Model).head;
				val localBasicElements = EcoreUtil2.getAllContentsOfType(rootElement, BasicModelElement)
				
				return new FilteringScope(super.getScope(context, reference), 
						[e | model?.includes.includedViaImports(e) || localBasicElements.contains(e.EObjectOrProxy) ])
			}			
		}

		else if ( context instanceof FilteredRelationShip) {
			
			val rootElement = EcoreUtil2.getRootContainer(context)
			return Scopes.scopeFor(EcoreUtil2.getAllContentsOfType(rootElement, BasicModelElement))			
		}

		else if (context instanceof SoftwareSystemInstance && reference == C4DslPackage.Literals.SOFTWARE_SYSTEM_INSTANCE__SOFTWARE_SYSTEM) {

			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, SoftwareSystem);

			return Scopes.scopeFor(candidates);
			
		}

		else if (context instanceof ContainerInstance && reference == C4DslPackage.Literals.CONTAINER_INSTANCE__CONTAINER) {

			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, Container);

			return Scopes.scopeFor(candidates);
			
		}
		
		else if ( context instanceof FilteredView && reference == C4DslPackage.Literals.FILTERED_VIEW__BASE_KEY) {
			
			val rootElement = EcoreUtil2.getRootContainer(context);
			val candidates = EcoreUtil2.getAllContentsOfType(rootElement, StaticView)
						
			return Scopes.scopeFor(candidates);
			
		}
				
		else {
			return super.getScope(context, reference);
		}

	}
	
}


