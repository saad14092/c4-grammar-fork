package de.systemticks.c4dsl.ls.provider;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import de.systemticks.c4dsl.ls.model.C4TokensConfig;
import de.systemticks.c4dsl.ls.model.C4TokensLoader;
import de.systemticks.c4dsl.ls.model.C4TokensConfig.C4TokenScope;
import de.systemticks.c4dsl.ls.model.C4ObjectWithContext;
import de.systemticks.c4dsl.ls.utils.C4Utils;
import lombok.AllArgsConstructor;
import lombok.Data;

public class C4CompletionProvider {

    private static final String EXPR_RELATIONSHIP = "->";

    private static final Logger logger = LoggerFactory.getLogger(C4CompletionProvider.class);    
    private final static List<CompletionItem> EMPTY = Collections.emptyList();    
    private Map<String, List<CompletionItem>> completionItemsPerScope;
    private List<String> relationRelevantScopes;
    
    public C4CompletionProvider() {
        init();
    }

    void init() {
        C4TokensLoader configLoader = new C4TokensLoader();
        C4TokensConfig config = configLoader.readConfiguration();
        if(config != null) {
            completionItemsPerScope = new HashMap<>();
            config.getScopes().forEach( scope -> {
                completionItemsPerScope.put(scope.getName(), keyWordCompletion(scope.getKeywords()));
            });
            relationRelevantScopes = config.getScopes().stream()
                                        .filter(C4TokenScope::isRelations)
                                        .map(C4TokenScope::getName)
                                        .collect(Collectors.toList());
        }
    }

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

        return C4Utils.merge(
                    completionItemsPerScope.getOrDefault(scope, EMPTY), 
                    relationRelevantScopes.contains(scope) ? identifierCompletion(getIdentifiers(model)) : EMPTY);
    }

    private List<CompletionItem> completionLineIsNotEmpty(String scope, String line, C4DocumentModel model, Position position) {

        Optional<String> leftFromCursor = C4Utils.leftFromCursor(line, position.getCharacter());

        if(leftFromCursor.isPresent()) {
            switch(scope) {
                case "ViewsDslContext":
                    return completionWithinViewsScope(leftFromCursor.get() , model);
                case "ModelDslContext":
                    return completionWithinModelScope(leftFromCursor.get() , model);
                default:
                    logger.info("Currently not completion for scope {} in line {} at pos {}", scope, line, position.getCharacter());
                    return EMPTY;
            }    
        }

        return EMPTY;
    }

    private List<CompletionItem> completionWithinModelScope(String line, C4DocumentModel model) {
        if(line.endsWith(EXPR_RELATIONSHIP)) {
            return identifierCompletion(getIdentifiers(model));
        }
        else {
            return EMPTY;
        }
    }

    private List<CompletionItem> completionWithinViewsScope(String line, C4DocumentModel model) {

        if(line.equals("SoftwareSystemDslContext") || line.endsWith( "ContainerDslContext")) {
            return completionInViewIdentifiers(model, (element) -> element.getObject() instanceof SoftwareSystem);
        }
        else if(line.equals( "ComponentDslContext")) {
            return completionInViewIdentifiers(model, (element) -> element.getObject() instanceof Container);
        }

        else {
            return EMPTY;
        }

    }

    private List<CompletionItem> completionInViewIdentifiers(C4DocumentModel model, Predicate<C4ObjectWithContext<Element>> func) {
        return identifierCompletion( 
                model.getAllElements().stream()
                    .map(Entry::getValue)
                    .filter(element -> func.test(element))
                    .map(C4ObjectWithContext::getIdentifier)
                    .collect(Collectors.toList())
            );
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
        return createSnippet(new SnippetData("Person Template", "Add a new person", "${1:identifier} = person ${2:name} \"Your Description\""));
    }

    private CompletionItem createSnippet(SnippetData snippet) {
        CompletionItem item = new CompletionItem();
        item.setLabel(snippet.getLabel());
        item.setDetail(snippet.getDetail());
        item.setKind(CompletionItemKind.Snippet);
        item.setInsertTextFormat(InsertTextFormat.Snippet);
        item.setInsertText(snippet.getInsertText());
        return item;
    }

    private List<CompletionItem> identifierCompletion(List<String> identifier) {
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

    @Data
    @AllArgsConstructor
    class SnippetData {
        private String label;
        private String detail;
        private String insertText;
    }
}
