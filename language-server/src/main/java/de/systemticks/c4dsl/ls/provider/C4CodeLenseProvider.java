package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.Workspace;
import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import com.structurizr.view.View;

import de.systemticks.c4dsl.ls.generator.C4Generator;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4CodeLenseProvider {

    private static final Logger logger = LoggerFactory.getLogger(C4CodeLenseProvider.class);

	public static BiFunction<Workspace, View, Command> toStructurizr = (workspace, view) -> {
		Command command = new Command("$(link-external) Show as Structurizr Diagram", "c4.show.diagram");
		command.setArguments(new ArrayList<Object>());
		try {
			command.getArguments().add(C4Generator.generateEncodedWorkspace(workspace));
		} catch (Exception e) {
			e.printStackTrace();
		}
		command.getArguments().add(view.getKey());
		return command;
	};

	public static BiFunction<Workspace, View, Command> toPlantUML = (workspace, view) -> {
		Command command = new Command("$(link-external) Show as PlantUML Diagram", "c4.show.plantuml");
		command.setArguments(new ArrayList<Object>());
		try {
			command.getArguments().add(C4Generator.generateEncodedPlantUml(view, new StructurizrPlantUMLExporter()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return command;
	};

	public List<CodeLens> calcCodeLenses(C4DocumentModel c4, String renderer) {
		
		BiFunction<Workspace, View, Command> func = renderer.contentEquals(C4Utils.RENDERER_STRUCTURIZR) ? C4CodeLenseProvider.toStructurizr : C4CodeLenseProvider.toPlantUML;

		if(!c4.isValid() && c4.getWorkspace() != null) {
			return Collections.emptyList();
		}
		
		return c4.getAllViews().stream().map( entry -> {
			int lineNumber = entry.getKey();
			String line = c4.getLineAt(lineNumber-1);
			int pos = C4Utils.findFirstNonWhitespace(line, 0, true);
			Range range = new Range(new Position(lineNumber-1, pos), new Position(lineNumber-1, pos));
			return new CodeLens(range, func.apply(c4.getWorkspace(), entry.getValue()), null);				
		}).collect(Collectors.toList());
	}

}
