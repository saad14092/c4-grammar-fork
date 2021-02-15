package de.systemticks.c4.ide.launch;

import java.util.Arrays;
import java.util.List;

import org.eclipse.xtext.ide.server.ServerLauncher;
import org.eclipse.xtext.ide.server.ServerModule;

import de.systemticks.c4.generator.C4GeneratorConfiguration;


public class C4ServerLauncher {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> list = Arrays.asList(args);
		int i = list.indexOf("-renderer");
		
		if(i != -1 && i+1 < list.size()) {
			String renderer = list.get(i+1);
			C4GeneratorConfiguration.INSTANCE.getInstance().setRenderer(renderer);
		}
		
		ServerLauncher.launch(ServerLauncher.class.getName(), args, new ServerModule());
	}

	
	
}
