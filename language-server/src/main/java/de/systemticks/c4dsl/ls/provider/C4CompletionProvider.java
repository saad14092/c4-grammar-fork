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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4CompletionProvider {

    private static final Logger logger = LoggerFactory.getLogger(C4CompletionProvider.class);

    private final static String WORKSPACE_SCOPE = "WorkspaceDslContext";
    private final static String MODEL_SCOPE = "ModelDslContext";
    private final static String SOFTWARE_SYSTEM_SCOPE = "SoftwareSystemDslContext";
    private final static String CONTAINER_SCOPE = "ContainerDslContext";
    private final static String VIEWS_SCOPE = "ViewsDslContext";
    private final static String RELATIONSHIP_STYLE_SCOPE = "RelationshipStyleDslContext";

    List<String> WORKSPACE_COMPLETION_KEYWORDS = Arrays.asList("properties", "!docs", "!adrs", "!identifiers",
            "!impliedRelationships", "model", "views", "configuration");
    List<String> MODEL_COMPLETION_KEYWORDS = Arrays.asList("enterprise", "group", "person", "softwareSystem",
            "deploymentEnvironment", "element");
    List<String> SOFTWARE_SYSTEM_COMPLETION_KEYWORDS = Arrays.asList("group", "container", "!docs", "!adrs",
            "description", "tags", "url", "properties", "perspectives");
    List<String> CONTAINER_COMPLETION_KEYWORDS = Arrays.asList("group", "component",
            "description", "tags", "url", "properties", "perspectives");

    List<String> VIEWS_COMPLETION_KEYWORDS = Arrays.asList("systemLandscape", "systemContext", "container", "component",
            "filtered", "dynamic", "custom", "styles", "theme", "themes", "branding", "terminology", "properties");
    List<String> RELATIONSHIP_STYLE_PROPERTIES = Arrays.asList("thickness", "color", "colour", "dashed", "style",
            "routing", "fontSize", "width", "position", "opacity");

    public List<CompletionItem> calcCompletions(C4DocumentModel model, Position position) {

        int lineNumber = position.getLine();
        String line = model.getLineAt(lineNumber);

        if (C4Utils.isBlank(line)) {
            String scope = model.getNearestScope(lineNumber + 1);
            switch (scope) {
                case WORKSPACE_SCOPE:
                    return completionForWorkspace();
                case MODEL_SCOPE:
                    return completionForModel(model);
                case SOFTWARE_SYSTEM_SCOPE:
                    return keyWordCompletion(SOFTWARE_SYSTEM_COMPLETION_KEYWORDS);
                case CONTAINER_SCOPE:
                    return keyWordCompletion(CONTAINER_COMPLETION_KEYWORDS);
                case VIEWS_SCOPE:
                    return keyWordCompletion(VIEWS_COMPLETION_KEYWORDS);
                case RELATIONSHIP_STYLE_SCOPE:
                    return propertyCompletion(RELATIONSHIP_STYLE_PROPERTIES);
                default:
                    logger.info("Currently not completion for scope {} implemented", scope);
            }
        }

        return Collections.emptyList();
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
        return keywords.stream().map(keyword -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(keyword);
            item.setKind(CompletionItemKind.Keyword);
            return item;
        }).collect(Collectors.toList());
    }

    private List<CompletionItem> propertyCompletion(List<String> properties) {
        return properties.stream().map(prop -> {
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
        return identifier.stream().map(id -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(id);
            item.setKind(CompletionItemKind.Reference);
            return item;
        }).collect(Collectors.toList());
    }

    List<String> getIdentifiers(C4DocumentModel model) {
        return model.getAllElements().stream()
                .map(ele -> ele.getValue().getIdentifier())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
