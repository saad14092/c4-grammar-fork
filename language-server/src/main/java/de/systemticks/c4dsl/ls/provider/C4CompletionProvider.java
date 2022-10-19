package de.systemticks.c4dsl.ls.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.SoftwareSystem;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.model.C4Tokens;
import de.systemticks.c4dsl.ls.model.C4ObjectWithContext;
import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4CompletionProvider {

    private static final Logger logger = LoggerFactory.getLogger(C4CompletionProvider.class);

    private final static String WORKSPACE_SCOPE = "WorkspaceDslContext";
    private final static String MODEL_SCOPE = "ModelDslContext";
    private final static String SOFTWARE_SYSTEM_SCOPE = "SoftwareSystemDslContext";
    private final static String CONTAINER_SCOPE = "ContainerDslContext";
    private final static String VIEWS_SCOPE = "ViewsDslContext";
    private final static String SYSTEM_CONTEXT_VIEW_SCOPE = "SystemContextViewDslContext";
    private final static String RELATIONSHIP_STYLE_SCOPE = "RelationshipStyleDslContext";

    private final static List<CompletionItem> EMPTY = Collections.emptyList();

    List<String> WORKSPACE_COMPLETION_KEYWORDS = Arrays.asList(
            C4Tokens.KW_PROPERTIES, C4Tokens.KW_DOCS, C4Tokens.KW_ADRS, C4Tokens.KW_IDENTIFIERS,
            C4Tokens.KW_IMPLIED_RELATIONSHIOS, C4Tokens.KW_MODEL, C4Tokens.KW_VIEWS, C4Tokens.KW_CONFIGURATION);

    List<String> MODEL_COMPLETION_KEYWORDS = Arrays.asList(
            C4Tokens.KW_ENTERPRISE, C4Tokens.KW_GROUP,C4Tokens.KW_PERSON, C4Tokens.KW_SOFTWARE_SYSTEM, 
            C4Tokens.KW_DEPLOYMENT_ENVIRONMENT, C4Tokens.KW_ELEMENT);

    List<String> SOFTWARE_SYSTEM_COMPLETION_KEYWORDS = Arrays.asList(
            C4Tokens.KW_GROUP, C4Tokens.KW_CONTAINER, C4Tokens.KW_DOCS, C4Tokens.KW_ADRS,
            C4Tokens.KW_DESCRIPTION, C4Tokens.KW_TAGS, C4Tokens.KW_URL, C4Tokens.KW_PROPERTIES, C4Tokens.KW_PERSPECTIVES);

    List<String> CONTAINER_COMPLETION_KEYWORDS = Arrays.asList(
            C4Tokens.KW_GROUP, C4Tokens.KW_COMPONENT,
            C4Tokens.KW_DESCRIPTION, C4Tokens.KW_TAGS, C4Tokens.KW_URL, C4Tokens.KW_PROPERTIES, C4Tokens.KW_PERSPECTIVES);

    List<String> VIEWS_COMPLETION_KEYWORDS = Arrays.asList(
            C4Tokens.KW_SYSTEM_LANSCAPE, C4Tokens.KW_SYSTEM_CONTEXT, C4Tokens.KW_CONTAINER, C4Tokens.KW_COMPONENT,
            C4Tokens.KW_FILTERED, C4Tokens.KW_DYNAMIC, C4Tokens.KW_CUSTOM, C4Tokens.KW_STYLES, C4Tokens.KW_THEME, 
            C4Tokens.KW_THEMES, C4Tokens.KW_BRANDING, C4Tokens.KW_TERMINOLOGY, C4Tokens.KW_PROPERTIES);

    List<String> VIEW_COMPLETION_COMMON_KEYWORDS = Arrays.asList(
            C4Tokens.KW_INCLUDE, C4Tokens.KW_EXCLUDE, C4Tokens.KW_ANIMATION, C4Tokens.KW_AUTOLAYOUT, C4Tokens.KW_TITLE);

    List<String> RELATIONSHIP_STYLE_PROPERTIES = Arrays.asList("thickness", "color", "colour", "dashed", "style",
            "routing", "fontSize", "width", "position", "opacity");

    public List<CompletionItem> calcCompletions(C4DocumentModel model, Position position) {

        int lineNumber = position.getLine();
        String line = model.getLineAt(lineNumber);
        String scope = model.getSurroundingScope(lineNumber + 1);

        if(scope.equals(C4DocumentModel.NO_SCOPE)) {
            return EMPTY;
        }

        if (C4Utils.isBlank(line)) {
            return completionLineIsEmpty(scope, model);
        }
        else {
            return completionLineIsNotEmpty(scope, line, model, position);
        }
    }

    private List<CompletionItem> completionLineIsEmpty(String scope, C4DocumentModel model) {
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
            case SYSTEM_CONTEXT_VIEW_SCOPE:
                return keyWordCompletion(VIEW_COMPLETION_COMMON_KEYWORDS);
            case RELATIONSHIP_STYLE_SCOPE:
                return propertyCompletion(RELATIONSHIP_STYLE_PROPERTIES);
            default:
                logger.info("Currently not completion for scope {} implemented", scope);
                return EMPTY;
        }
    }

    private List<CompletionItem> completionLineIsNotEmpty(String scope, String line, C4DocumentModel model, Position position) {

        switch(scope) {
            case VIEWS_SCOPE:
                return completionWithinViewsScope(C4Utils.leftOfCursor(line, position.getCharacter()) , model);
            case MODEL_SCOPE:
                return completionWithinModelScope(C4Utils.leftOfCursor(line, position.getCharacter()) , model);
            default:
                logger.info("Currently not completion for scope {} in line {} at pos {}", scope, line, position.getCharacter());
                return EMPTY;
        }

    }

    private List<CompletionItem> completionWithinModelScope(String line, C4DocumentModel model) {
        if(line.endsWith(C4Tokens.EXPR_RELATIONSHIP)) {
            return identifierInModelCompletion(getIdentifiers(model));
        }
        else {
            return EMPTY;
        }
    }

    private List<CompletionItem> completionWithinViewsScope(String line, C4DocumentModel model) {

        if(line.equals( C4Tokens.KW_SYSTEM_CONTEXT) || line.endsWith( C4Tokens.KW_CONTAINER)) {
            return completionInViewIdentifiers(model, (element) -> element.getObject() instanceof SoftwareSystem);
        }
        else if(line.equals( C4Tokens.KW_COMPONENT)) {
            return completionInViewIdentifiers(model, (element) -> element.getObject() instanceof Container);
        }

        else {
            return EMPTY;
        }

    }

    private List<CompletionItem> completionInViewIdentifiers(C4DocumentModel model, Predicate<C4ObjectWithContext<Element>> func) {
        return identifierInModelCompletion( 
                model.getAllElements().stream()
                    .map(Entry::getValue)
                    .filter(element -> func.test(element))
                    .map(C4ObjectWithContext::getIdentifier)
                    .collect(Collectors.toList())
            );
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

    private CompletionItem workspaceSnippet() {
        CompletionItem item = new CompletionItem();
        item.setLabel("Workspace Template");
        item.setDetail("Add a workspace");
        item.setKind(CompletionItemKind.Snippet);
        item.setInsertTextFormat(InsertTextFormat.Snippet);
        item.setInsertText("workspace {\n\n}");
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
