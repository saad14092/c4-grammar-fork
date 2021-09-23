package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.view.View;

import de.systemticks.c4dsl.ls.generator.C4Generator;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4CodeLenseProvider {

    private static final Logger logger = LoggerFactory.getLogger(C4CodeLenseProvider.class);

	public List<CodeLens> calcCodeLenses(C4DocumentModel c4) {

		List<CodeLens> codeLenses = new ArrayList<>();
		
		if(!c4.isValid()) {
			return codeLenses;
		}
		
		try {
			String jsonWorkspace = C4Generator.generateEncodedWorkspace(c4.getWorkspace());
			c4.getViewToLineNumbers().forEach( entry -> {
				int lineNumber = entry.getKey();
				String line = c4.getLineAt(lineNumber);
				int pos = C4Utils.findFirstNonWhitespace(line, 0, true);
				Range range = new Range(new Position(lineNumber-1, pos), new Position(lineNumber-1, pos));
				Command command = new Command("$(link-external) Show as Structurizr Diagram", "c4.show.diagram");
				command.setArguments(new ArrayList<Object>());
				command.getArguments().add(jsonWorkspace);
				View view = entry.getValue();
				command.getArguments().add(view.getKey());
				codeLenses.add(new CodeLens(range, command, null));				
			});
		} catch (Exception e1) {
			logger.error("Cannot create json format from workspace {}", e1.getMessage());
			return codeLenses;
		}
		return codeLenses;			
	}

}
