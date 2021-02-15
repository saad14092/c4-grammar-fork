package de.systemticks.c4.ide.command;

import java.util.List;

import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.ide.server.commands.IExecutableCommandService;
import org.eclipse.xtext.util.CancelIndicator;

import com.google.common.collect.Lists;
import com.google.gson.JsonPrimitive;

import de.systemticks.c4.generator.C4GeneratorConfiguration;

public class C4CommandService implements IExecutableCommandService{

	@Override
	public Object execute(ExecuteCommandParams params, ILanguageServerAccess access, CancelIndicator cancelIndicator) {
		
		if ("c4.generator.type".equals(params.getCommand())) {	
			
			String renderer = ((JsonPrimitive)params.getArguments().get(0)).getAsString();
			C4GeneratorConfiguration.INSTANCE.getInstance().setRenderer(renderer);
			
			return "c4.generator.type";
		}
		
		return "Unknown Command";
		
	}

	@Override
	public List<String> initialize() {
		// TODO Auto-generated method stub
		return Lists.newArrayList("c4.generator.type");
	}

}
