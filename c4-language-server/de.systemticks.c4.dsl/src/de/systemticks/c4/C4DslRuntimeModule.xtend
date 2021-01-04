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
package de.systemticks.c4

import org.eclipse.xtext.generator.IContextualOutputConfigurationProvider
import org.eclipse.xtext.generator.IOutputConfigurationProvider
import de.systemticks.c4.generator.C4DslOutputConfiguration
import org.eclipse.xtext.naming.IQualifiedNameProvider
import de.systemticks.c4.scoping.C4DslQualifiedNameProvider

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
class C4DslRuntimeModule extends AbstractC4DslRuntimeModule {

	def Class<? extends IOutputConfigurationProvider> bindIOutputConfigurationProvider() {
		return C4DslOutputConfiguration;
	}	
	
	def Class<? extends IContextualOutputConfigurationProvider> bindIContextualOutputConfigurationProvider() {
		return C4DslOutputConfiguration;
	}	

	override Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return C4DslQualifiedNameProvider
	}

    
}
