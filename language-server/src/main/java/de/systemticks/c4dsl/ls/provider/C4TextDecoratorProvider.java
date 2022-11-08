package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.ContainerInstance;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.Element;
import com.structurizr.model.InfrastructureNode;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.SoftwareSystemInstance;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.model.C4ObjectWithContext;
import de.systemticks.c4dsl.ls.utils.LineToken;
import de.systemticks.c4dsl.ls.utils.LineTokenizer;
import lombok.Data;

public class C4TextDecoratorProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(C4TextDecoratorProvider.class);
    private LineTokenizer tokenizer = new LineTokenizer();

    public List<DecoratorRange> calculateDecorations(C4DocumentModel model) {

        List<DecoratorRange> result = new ArrayList<>();

        if(model == null || !model.isValid()) {
            logger.warn("Cannot calculate text decorations");
            return result;
        }

        model.getAllElements().forEach( entry -> {
            
            String line = model.getLineAt(lineNumber(entry.getKey()));
            C4ObjectWithContext<Element> element = entry.getValue();

            result.addAll(calculateDecoratorsForElement(element, line, lineNumber(entry.getKey())));

        });

        model.getAllRelationships().forEach( entry -> {
            String line = model.getLineAt(lineNumber(entry.getKey()));
            Relationship relationShip = entry.getValue().getObject();

            result.addAll(calculateDecoratorsForRelationship(relationShip, line, lineNumber(entry.getKey())));
        });

        return result;
    }

    public List<DecoratorRange> calculateDecoratorsForRelationship(Relationship relationship, String line, int lineNumber) {
        return decorationsForRelationship(line, lineNumber, RELATIONSHIP_DECORATIONS);
    }

    List<DecoratorRange> decorationsForRelationship(String line, int lineNumber, String[] decorationLabels) {

        List<LineToken> tokens = tokenizer.tokenize(line);
        int firstIndex = (tokens.get(1).getToken().equals("->")) ? 3 : 2;

        return IntStream.range(0, decorationLabels.length)
            .filter( i -> firstIndex + i < tokens.size() && !tokens.get(firstIndex + i).getToken().equals("{"))
            .mapToObj( i -> createDecoratorRange(decorationLabels[i], lineNumber, tokens.get(firstIndex + i).getStart()))
            .collect(Collectors.toList());

    }

    List<DecoratorRange> decorationsForElement(String line, int lineNumber, String[] decorationLabels) {

        List<LineToken> tokens = tokenizer.tokenize(line);
        int firstIndex = (tokens.get(1).getToken().equals("=")) ? 3 : 1;

        return IntStream.range(0, decorationLabels.length)
            .filter( i -> firstIndex + i < tokens.size() && !tokens.get(firstIndex + i).getToken().equals("{"))
            .mapToObj( i -> createDecoratorRange(decorationLabels[i], lineNumber, tokens.get(firstIndex + i).getStart()))
            .collect(Collectors.toList());
    }

    private final static String[] PERSON_DECORATIONS = new String[] {"name: ", "description: ", "tags: "};
    List<DecoratorRange> decorationsForPerson(String line, int lineNumber) {
        return decorationsForElement(line, lineNumber, PERSON_DECORATIONS);
    }

    private final static String[] SOFTWARE_SYSTEM_DECORATIONS = new String[] {"name: ", "description: ", "tags: "};
    List<DecoratorRange> decorationsForSoftwareSystem(String line, int lineNumber) {
        return decorationsForElement(line, lineNumber, SOFTWARE_SYSTEM_DECORATIONS);
    }

    private final static String[] CONTAINER_DECORATIONS = new String[] {"name: ", "description: ", "technology: ", "tags: "};
    List<DecoratorRange> decorationsForContainer(String line, int lineNumber) {
        return decorationsForElement(line, lineNumber, CONTAINER_DECORATIONS);
    }

    private final static String[] COMPONENT_DECORATIONS = new String[] {"name: ", "description: ", "technology: ", "tags: "};
    List<DecoratorRange> decorationsForComponent(String line, int lineNumber) {
        return decorationsForElement(line, lineNumber, COMPONENT_DECORATIONS);
    }

    private final static String[] DEPLOYMENT_NODE_DECORATIONS = new String[] {"name: ", "description: ", "technology: ", "tags: ", "instances: "};
    List<DecoratorRange> decorationsForDeploymentNode(String line, int lineNumber) {
        return decorationsForElement(line, lineNumber, DEPLOYMENT_NODE_DECORATIONS);
    }

    private final static String[] INFRASTRUCUTRE_NODE_DECORATIONS = new String[] {"name: ", "description: ", "technology: ", "tags: "};
    List<DecoratorRange> decorationsForInfrastructureNode(String line, int lineNumber) {
        return decorationsForElement(line, lineNumber, INFRASTRUCUTRE_NODE_DECORATIONS);
    }

    private final static String[] SOFTWARE_SYSTEM_INSTANCE_DECORATIONS = new String[] {"identifier: ", "deploymentGroups: ", "tags: "};
    List<DecoratorRange> decorationsForSoftwareSystemInstance(String line, int lineNumber) {
        return decorationsForElement(line, lineNumber, SOFTWARE_SYSTEM_INSTANCE_DECORATIONS);
    }

    private final static String[] CONTAINER_INSTANCE_DECORATIONS = new String[] {"identifier: ", "deploymentGroups: ", "tags: "};
    List<DecoratorRange> decorationsForContainerInstance(String line, int lineNumber) {
        return decorationsForElement(line, lineNumber, CONTAINER_INSTANCE_DECORATIONS);
    }

    private final static String[] RELATIONSHIP_DECORATIONS = new String[] {"description: ", "technology: ", "tags: "};

    public List<DecoratorRange> calculateDecoratorsForElement(C4ObjectWithContext<Element> elementWithContext, String line, int lineNumber) {
        
        List<DecoratorRange> decorations = new ArrayList<>();

        Element context = elementWithContext.getObject();
        
        if(context instanceof Person) {
            decorations = decorationsForPerson(line, lineNumber);
        }
        else if(context instanceof SoftwareSystem) {
            decorations = decorationsForSoftwareSystem(line, lineNumber);
        }

        else if(context instanceof Container) {
            decorations = decorationsForContainer(line, lineNumber);
        }

        else if(context instanceof Component) {
            decorations = decorationsForComponent(line, lineNumber);
        }

        else if(context instanceof DeploymentNode) {
            decorations = decorationsForDeploymentNode(line, lineNumber);
        }

        else if(context instanceof InfrastructureNode) {
            decorations = decorationsForInfrastructureNode(line, lineNumber);
        }

        else if(context instanceof SoftwareSystemInstance) {
            decorations = decorationsForSoftwareSystemInstance(line, lineNumber);
        }

        else if(context instanceof ContainerInstance) {
            decorations = decorationsForContainerInstance(line, lineNumber);
        }

        return decorations;
    }
     
    private DecoratorRange createDecoratorRange(String type, int line, int character) {
       return new DecoratorRange(type ,new Range(new Position(line, character), new Position(line, character)));
    }

    private int lineNumber(Integer key) {
        return key-1;
    }
 
    @Data
    public class DecoratorRange {
        
        private String type;
        
        private Range range;
        
        public DecoratorRange(String type, Range range) {
            this.type = type;
            this.range = range;        
        }

    }
    
}

