package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.model.C4ObjectWithContext;

public class C4TextDecoratorProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(C4TextDecoratorProvider.class);

    public List<DecoratorRange> calculateDecorations(C4DocumentModel model) {

        List<DecoratorRange> result = new ArrayList<>();

        if(model == null || !model.isValid()) {
            logger.warn("Cannot calculate text decorations");
            return result;
        }

        model.getAllElements().forEach( entry -> {
            
            String line = model.getLineAt(lineNumber(entry));
            Element element = entry.getValue().getObject();

            result.addAll(calculateDecoratorsForOneElement(element, line, lineNumber(entry)));

        });

        return result;
    }

    public List<DecoratorRange> calculateDecoratorsForOneElement(Element element, String line, int lineNumber) {
        
        List<DecoratorRange> result = new ArrayList<>();

        int fromIndex = 0;

        PositionAndLength posObject = calculatePositionAndLengthObject(element, line, ele -> ele.getName(), fromIndex);
        if(posObject.getStartPos() > 0) {
            result.add(createDecoratorRange("name: ", lineNumber, posObject.getStartPos()));
            fromIndex = posObject.getFromIndex();
        }
        
        posObject = calculatePositionAndLengthObject(element, line, ele -> ele.getDescription(), fromIndex);
        if(posObject.getStartPos() > 0) {
            result.add(createDecoratorRange("description: ", lineNumber, posObject.getStartPos()));
            fromIndex = posObject.getFromIndex();
        }

        posObject = calculatePositionAndLengthObject(element, line, ele ->  getTechnology(ele).orElse(null), fromIndex);
        if(posObject.getStartPos() > 0) {
            result.add(createDecoratorRange("technology: ", lineNumber, posObject.getStartPos()));
            fromIndex = posObject.getFromIndex();
        }

        posObject = calculatePositionAndLengthObject(element, line, ele ->  getFirstCustomTag(ele).orElse(null), fromIndex);
        if(posObject.getStartPos() > 0) {
            result.add(createDecoratorRange("tags: ", lineNumber, posObject.getStartPos()));
            fromIndex = posObject.getFromIndex();
        }

        return result;
    }

    private Optional<String> getTechnology(Element element) {
        if(element instanceof Component) {
            return Optional.ofNullable(((Component) element).getTechnology());
        }
        else if(element instanceof Container) {
            return Optional.ofNullable(((Container) element).getTechnology());
        }
        return Optional.empty();
    }
     
    Optional<String> getFirstCustomTag(Element element) {

        return Arrays.asList(element.getTags().split(",")).stream()
            .map(String::trim)
            .filter( tag -> !element.getDefaultTags().contains(tag))
            .findFirst();

    }

    private DecoratorRange createDecoratorRange(String type, int line, int character) {
       return new DecoratorRange(type ,new Range(new Position(line, character), new Position(line, character)));
    }

    PositionAndLength calculatePositionAndLengthObject(Element element, String line, Function<Element, String> func, int fromIndex) {        
        String decoratable = func.apply(element);
        if(decoratable == null || decoratable.length() == 0) {
            return new PositionAndLength(-1, fromIndex);
        }
        int startPos = line.indexOf(decoratable, fromIndex)-1;
        return new PositionAndLength(startPos, startPos + decoratable.length());
    }

    private int lineNumber(Entry<Integer, C4ObjectWithContext<Element>> entry) {
        return entry.getKey()-1;
    }

    class PositionAndLength {

        private int startPos;
        private int fromIndex;
        
        public PositionAndLength(int startPos, int fromIndex) {
            this.startPos = startPos;
            this.fromIndex = fromIndex;
        }
        public int getStartPos() {
            return startPos;
        }
        public int getFromIndex() {
            return fromIndex;
        }
        
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

