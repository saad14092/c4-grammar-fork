package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import com.structurizr.model.Element;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.model.C4ObjectWithContext;

public class C4TextDecoratorProvider {
    

    public List<DecoratorRange> calculateDecorations(C4DocumentModel model) {
        return calculateDecorationsForModelNames2(model);
    }

    public List<DecoratorRange> calculateDecorationsForModelNames(C4DocumentModel model) {
        return model.getAllElements().stream().map( entry -> {            
            int startPos = calcStartPos(entry, model.getLineAt(lineNumber(entry)), ele -> ele.getName(), 0);
            return new DecoratorRange("name: ", new Range(new Position(entry.getKey()-1, startPos), new Position(entry.getKey()-1, startPos)));
        }).collect(Collectors.toList());
    }

    public List<DecoratorRange> calculateDecorationsForModelNames2(C4DocumentModel model) {

        List<DecoratorRange> result = new ArrayList<>();

        model.getAllElements().forEach( entry -> {
            String line = model.getLineAt(lineNumber(entry));
            int startPos = calcStartPos(entry, line, ele -> ele.getName(), 0);
            result.add(new DecoratorRange("name: ", new Range(new Position(entry.getKey()-1, startPos), new Position(entry.getKey()-1, startPos))));
            int fromIndex = startPos + entry.getValue().getObject().getName().length();
            startPos = calcStartPos(entry, line, ele -> ele.getDescription(), fromIndex);
            if(startPos > 0) {
                result.add(new DecoratorRange("description: ", new Range(new Position(entry.getKey()-1, startPos), new Position(entry.getKey()-1, startPos))));
            }
        });

        return result;
    }

    private int calcStartPos(Entry<Integer, C4ObjectWithContext<Element>> entry, String line, Function<Element, String> func, int fromIndex) {        
        Element element = entry.getValue().getObject();
        String decoratable = func.apply(element);
        if(decoratable == null || decoratable.length() == 0) {
            return -1;
        }
        return line.indexOf(decoratable, fromIndex)-1;
    }

    private int lineNumber(Entry<Integer, C4ObjectWithContext<Element>> entry) {
        return entry.getKey()-1;
    }

    public class DecoratorRange {
        
        private String type;
        
        private Range range;
        
        public DecoratorRange(String type, Range range) {
            this.type = type;
            this.range = range;        
        }
        
        public String getType() {
            return type;
        }
        
        public Range getRange() {
            return range;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setRange(Range range) {
            this.range = range;
        }
        
    }
    
}

