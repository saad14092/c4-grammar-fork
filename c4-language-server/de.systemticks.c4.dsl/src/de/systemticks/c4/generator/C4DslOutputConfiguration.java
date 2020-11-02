package de.systemticks.c4.generator;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.generator.OutputConfigurationProvider;

public class C4DslOutputConfiguration extends OutputConfigurationProvider {

	public static final String PLANTUML_OUTPUT = "plantuml-output";

	@Override
	public Set<OutputConfiguration> getOutputConfigurations() {

		OutputConfiguration plantUmlOutput = new OutputConfiguration(PLANTUML_OUTPUT);
		plantUmlOutput.setDescription("Output folder for generated plant uml code");
		plantUmlOutput.setOutputDirectory("./plantuml-gen");
		plantUmlOutput.setOverrideExistingResources(true);
		plantUmlOutput.setCreateOutputDirectory(true);
		plantUmlOutput.setCanClearOutputDirectory(false);
		plantUmlOutput.setCleanUpDerivedResources(true);
		plantUmlOutput.setSetDerivedProperty(true);
		plantUmlOutput.setKeepLocalHistory(false);

		return newHashSet(plantUmlOutput);
	}

}
