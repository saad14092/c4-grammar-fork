package de.systemticks.c4dsl.ls.provider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import de.systemticks.c4dsl.ls.model.C4TokensConfig;
import de.systemticks.c4dsl.ls.model.C4TokensLoader;
import de.systemticks.c4dsl.ls.model.C4TokensConfig.C4TokenScope;
import de.systemticks.c4dsl.ls.model.C4ObjectWithContext;
import de.systemticks.c4dsl.ls.utils.C4Utils;
import de.systemticks.c4dsl.ls.utils.LineToken;
import lombok.AllArgsConstructor;
import lombok.Data;

public class C4CompletionProvider {

    private static final String EXPR_RELATIONSHIP = "->";
    private static final String EXPR_ASSIGNMENT = "=";

    private static final Logger logger = LoggerFactory.getLogger(C4CompletionProvider.class);    
    private final static List<CompletionItem> NO_COMPLETIONS = Collections.emptyList();    
    private Map<String, List<CompletionItem>> completionItemsPerScope;
    private List<String> relationRelevantScopes;
    
    public C4CompletionProvider(C4TokensLoader configLoader) {
        init(configLoader);
    }

    void init(C4TokensLoader configLoader) {
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
        String scope = model.getSurroundingScope(lineNumber);

        if(scope.equals(C4DocumentModel.NO_SCOPE)) {
            logger.warn("Cannot calculate code completion. No scope detected for line {} at Position ({},{})", line, position.getLine(), position.getCharacter());
            return NO_COMPLETIONS;
        }

        List<LineToken> tokens = C4Utils.tokenize(line);

        // Line is empty or cursor is located before first token. 
        // Determine all keywords in the given scope and potential identifer references (if applicable)
        if(tokens.isEmpty() || C4Utils.cursorBeforeToken(tokens.get(0), position.getCharacter())) {
            return completeBeforeFirstToken(scope, model);
        } 

        else if(tokens.size() == 1 && C4Utils.cursorInsideToken(tokens.get(0), position.getCharacter())) {
            return completeBeforeFirstToken(scope, model).stream()
                    .filter( item -> item.getLabel().startsWith(tokens.get(0).getToken()))
                    .collect(Collectors.toList());
        }

        else {
            switch(scope) {
                case "ModelDslContext":
                case "EnterpriseDslContext":
                case "PersonDslContext":
                case "SoftwareSystemDslContext":
                case "ContainerDslContext":
                case "ComponentDslContext":
                case "DeploymentEnvironmentDslContext":
                case "DeploymentNodeDslContext":
                case "InfrastructureNodeDslContext":
                case "SoftwareSystemInstanceDslContext":
                case "ContainerInstanceDslContext":
                    return completeModel(scope, tokens, position, model);
                case "ViewsDslContext":
                    return completeViews(tokens, position, model);
                default:
                    return NO_COMPLETIONS;
            }         
        }
    }

    private List<CompletionItem> completeBeforeFirstToken(String scope, C4DocumentModel model) {

        return C4Utils.merge(
                    completionItemsPerScope.getOrDefault(scope, NO_COMPLETIONS), 
                    relationRelevantScopes.contains(scope) ? identifierCompletion(getIdentifiers(model)) : NO_COMPLETIONS);
    }

    private List<CompletionItem> completeModel(String scope, List<LineToken> tokens, Position cursor, C4DocumentModel docModel) {

        if(tokens.size() == 2 && tokens.get(1).getToken().equals(EXPR_ASSIGNMENT)) {
            return completeBeforeFirstToken(scope, docModel).stream()
                    .collect(Collectors.toList());
        }

        else if(tokens.size() == 3 && tokens.get(1).getToken().equals(EXPR_ASSIGNMENT) && C4Utils.cursorInsideToken(tokens.get(2), cursor.getCharacter())) {
            return completeBeforeFirstToken(scope, docModel).stream()
                    .filter( item -> item.getLabel().startsWith(tokens.get(2).getToken()))
                    .collect(Collectors.toList());
        }

        else if(tokens.size() == 2 && tokens.get(1).getToken().equals(EXPR_RELATIONSHIP)) {
            return identifierCompletion(getIdentifiers(docModel));
        }

        else if(tokens.size() == 3 && tokens.get(1).getToken().equals(EXPR_RELATIONSHIP) && C4Utils.cursorInsideToken(tokens.get(2), cursor.getCharacter())) {
            return identifierCompletion(getIdentifiers(docModel)).stream()
                    .filter( item -> item.getLabel().startsWith(tokens.get(2).getToken()))
                    .collect(Collectors.toList());
        }

        return NO_COMPLETIONS;
    }

    private List<CompletionItem> completeViews(List<LineToken> tokens, Position cursor, C4DocumentModel docModel) {

        List<CompletionItem> completionIds = NO_COMPLETIONS;

        if(tokens.size() <= 2) {
            LineToken firstToken = tokens.get(0);

            if(firstToken.getToken().equals("systemContext")) {
                completionIds = completionInViewIdentifiers(docModel, (element) -> element.getObject() instanceof SoftwareSystem);
            }
            else if(firstToken.getToken().equals("container")) {
                completionIds = completionInViewIdentifiers(docModel, (element) -> element.getObject() instanceof SoftwareSystem);
            }
            else if(firstToken.getToken().equals("component")) {
                completionIds = completionInViewIdentifiers(docModel, (element) -> element.getObject() instanceof Container);
            }
            else if(firstToken.getToken().equals("dynamic")) {
                completionIds = completionInViewIdentifiers(docModel, (element) -> element.getObject() instanceof Container || element.getObject() instanceof SoftwareSystem);
                completionIds.add(new CompletionItem("*"));
            }
            else if(firstToken.getToken().equals("deployment")) {
                completionIds = completionInViewIdentifiers(docModel, (element) -> element.getObject() instanceof SoftwareSystem);
                completionIds.add(new CompletionItem("*"));
            }

            if(tokens.size() == 2 && C4Utils.cursorInsideToken(tokens.get(1), cursor.getCharacter())) {
                completionIds = completionIds.stream()
                        .filter( item -> item.getLabel().startsWith(tokens.get(1).getToken()))
                        .collect(Collectors.toList());
            }    
        }

        return completionIds;
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
