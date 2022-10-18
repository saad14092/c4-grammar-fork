package de.systemticks.c4dsl.ls.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;

public class C4CompletionProvider {
    
    private final static String WORKSPACE_SCOPE = "WorkspaceDslContext";
    private final static String MODEL_SCOPE = "ModelDslContext";
    private final static String RELATIONSHIP_STYLE_SCOPE = "RelationshipStyleDslContext";

    List<String> WORKSPACE_COMPLETION_KEYWORDS = Arrays.asList("properties", "!docs", "!adrs", "!identifiers", "!impliedRelationships", "model", "views", "configuration");
    List<String> MODEL_COMPLETION_KEYWORDS = Arrays.asList("enterprise", "group", "person", "softwareSystem", "deploymentEnvironment", "element");

    List<String> RELATIONSHIP_STYLE_PROPERTIES = Arrays.asList("thickness", "color", "colour", "dashed", "style", "routing", "fontSize", "width", "position", "opacity");

    public List<CompletionItem> calcCompletions(C4DocumentModel model, Position position) {

        int lineNumber = position.getLine();
        String line = model.getLineAt(lineNumber);

        if(isBlank(line)) {
            String scope = model.getNearestScope(lineNumber+1);
            System.err.println("Scope "+scope);
            if(scope.equals(WORKSPACE_SCOPE)) {
                return completionForWorkspace();
            }
            else if(scope.equals(MODEL_SCOPE)) {
                return completionForModel(model);
            }
            else if(scope.equals(RELATIONSHIP_STYLE_SCOPE)) {
                return propertyCompletion(RELATIONSHIP_STYLE_PROPERTIES);
            }
        }

        return Collections.emptyList();
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private List<CompletionItem> completionForWorkspace() {
        return keyWordCompletion(WORKSPACE_COMPLETION_KEYWORDS);
    }

    private List<CompletionItem> completionForModel(C4DocumentModel documentModel) {
        List<CompletionItem> result = keyWordCompletion(MODEL_COMPLETION_KEYWORDS);
        result.add(personSnippet());
        result.addAll(identifierInModelCompletion(getIdentifiers(documentModel)));
        return result;
    }

    private List<CompletionItem> keyWordCompletion(List<String> keywords) {
        return keywords.stream().map( keyword -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(keyword);
            item.setKind(CompletionItemKind.Keyword);
            return item;
        }).collect(Collectors.toList());
    }

    private List<CompletionItem> propertyCompletion(List<String> properties) {
        return properties.stream().map( prop -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(prop);
            item.setKind(CompletionItemKind.Property);
            return item;
        }).collect(Collectors.toList());
    }

    private CompletionItem personSnippet() {
        CompletionItem item = new CompletionItem();
        item.setLabel("Person Template");
        item.setDetail("Add a new person");
        item.setKind(CompletionItemKind.Snippet);
        item.setInsertTextFormat(InsertTextFormat.Snippet);
        item.setInsertText("${1:identifier} = person ${2:name} \"Your Description\"");
        return item;
    }

    private List<CompletionItem> identifierInModelCompletion(List<String> identifier) {
        return identifier.stream().map( id -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(id);
            item.setKind(CompletionItemKind.Reference);
            return item;
        }).collect(Collectors.toList());
    }

    private List<String> getIdentifiers(C4DocumentModel model) {
        return model.getAllElements().stream()
            .map ( ele -> ele.getValue().getIdentifier())
            .filter( Objects::nonNull)
            .collect(Collectors.toList());
    }
}
