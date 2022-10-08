package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import com.structurizr.model.Element;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;

public class C4TextDecoratorProvider {
    
    public List<Range> calculateDecorationsForModelNames(C4DocumentModel model) {

        return model.getAllElements().stream().map( e -> {
            Element element = e.getValue().getObject();
            String name = element.getName();
            String entireLine = model.getLineAt(e.getKey()-1);
            int startPos = entireLine.indexOf(name);
            return new Range(new Position(e.getKey()-1, startPos-1), new Position(e.getKey()-1, startPos-1));
        }).collect(Collectors.toList());

    }

}
