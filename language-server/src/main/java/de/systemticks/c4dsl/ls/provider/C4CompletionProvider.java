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
import de.systemticks.c4dsl.ls.utils.LineTokenizer;
import de.systemticks.c4dsl.ls.utils.LineTokenizer.CursorLocation;

public class C4CompletionProvider {

    private static final String EXPR_RELATIONSHIP = "->";
    private static final String EXPR_ASSIGNMENT = "=";

    private static final Logger logger = LoggerFactory.getLogger(C4CompletionProvider.class);    
    private final static List<CompletionItem> NO_COMPLETIONS = Collections.emptyList();    
    private Map<String, List<CompletionItem>> keywordCompletions;
    private Map<String, List<CompletionItem>> snippetCompletions;
    private Map<String, List<CompletionItem>> detailCompletions;
    private List<String> relationRelevantScopes;
    private LineTokenizer tokenizer;
    private C4CompletionItemCreator completionCreator;
    
    public C4CompletionProvider(C4TokensLoader configLoader) {
        tokenizer = new LineTokenizer();
        init(configLoader);
    }

    void init(C4TokensLoader configLoader) {
        C4TokensConfig config = configLoader.readConfiguration();
        if(config != null) {
            completionCreator = new C4CompletionItemCreator();
            keywordCompletions = new HashMap<>();
            snippetCompletions = new HashMap<>();
            detailCompletions = new HashMap<>();
            config.getScopes().forEach( scope -> {
                keywordCompletions.put(scope.getName(), completionCreator.keyWordCompletion(scope.getKeywords()));                    
                if(scope.getSnippets() != null) {
                    snippetCompletions.put(scope.getName(), completionCreator.snippetCompletion(scope.getSnippets()));
                }                
            });
            config.getDetails().forEach( detail -> {
                detailCompletions.put(detail.getKeyword(), completionCreator.propertyCompletion(detail.getChoices()));
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
        List<CompletionItem> result;

        logger.debug("-> calcCompletions in scope {} at Position ({},{})", scope, position.getLine(), position.getCharacter());

        // if model is empy, i.e. just created
        if(C4Utils.isBlank(model.getRawText())) {
            result = C4Utils.merge(completeAsPerConfiguration("DocumentRootContext", model), snippetCompletions.getOrDefault("DocumentRootContext", NO_COMPLETIONS));
        }

        else if(scope.equals(C4DocumentModel.NO_SCOPE)) {
            logger.warn("Cannot calculate code completion. No scope detected");
            result = NO_COMPLETIONS;
        }

        else {
            List<LineToken> tokens = tokenizer.tokenize(line);
            CursorLocation cursorAt = tokenizer.cursorLocation(tokens, position.getCharacter());

            // Line is empty or cursor is located before first token. 
            // Determine all keywords in the given scope and potential identifer references (if applicable)
            if(tokens.isEmpty() || tokenizer.isBeforeToken(cursorAt, 0) ) {
                result = C4Utils.merge(completeAsPerConfiguration(scope, model), snippetCompletions.getOrDefault(scope, NO_COMPLETIONS));
            }

            else if(tokenizer.isInsideToken(cursorAt, 0)) {
                result = completeAsPerConfiguration(scope, model).stream()
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
                        result = completeModel(scope, tokens, cursorAt, model);
                        break;
                    case "ViewsDslContext":
                        result = completeViews(tokens, cursorAt, model);
                        break;
                    case "ElementStyleDslContext":
                    case "RelationshipStyleDslContext":
                        result = completeDetails(tokens, cursorAt, model);
                        break;
                    default:                
                        result = NO_COMPLETIONS;
                }         
            }
        }
        
        logger.debug("<- calcCompletions size = {}", result.size());

        return result;
    }

    private List<CompletionItem> completeAsPerConfiguration(String scope, C4DocumentModel model) {

        return C4Utils.merge(
                    keywordCompletions.getOrDefault(scope, NO_COMPLETIONS), 
                    relationRelevantScopes.contains(scope) ? completionCreator.identifierCompletion(getIdentifiers(model)) : NO_COMPLETIONS);
    }

    private List<CompletionItem> completeModel(String scope, List<LineToken> tokens, CursorLocation cursor, C4DocumentModel docModel) {

        if(tokens.size() >= 2) {
            if(tokens.get(1).getToken().equals(EXPR_ASSIGNMENT)) {
                if(tokenizer.isBetweenTokens(cursor, 1, 2)) {
                    return completeAsPerConfiguration(scope, docModel).stream().collect(Collectors.toList());
                }
                if(tokenizer.isInsideToken(cursor, 2)) {
                    return completeAsPerConfiguration(scope, docModel).stream()
                            .filter( item -> item.getLabel().startsWith(tokens.get(2).getToken()))
                            .collect(Collectors.toList());
                }
            }

            if(tokens.get(1).getToken().equals(EXPR_RELATIONSHIP)) {
                if(tokenizer.isBetweenTokens(cursor, 1, 2)) {
                    return completionCreator.identifierCompletion(getIdentifiers(docModel));
                }
                if(tokenizer.isInsideToken(cursor, 2)) {
                    return completionCreator.identifierCompletion(getIdentifiers(docModel)).stream()
                            .filter( item -> item.getLabel().startsWith(tokens.get(2).getToken()))
                            .collect(Collectors.toList());
                }
            }
        }

        return NO_COMPLETIONS;
    }

    private List<CompletionItem> completeDetails(List<LineToken> tokens, CursorLocation cursor, C4DocumentModel docModel) {

        LineToken firstToken = tokens.get(0);

        if(tokenizer.isBetweenTokens(cursor, 0, 1)) {
            return detailCompletions.getOrDefault(firstToken.getToken(), NO_COMPLETIONS);
        }

        if(tokenizer.isInsideToken(cursor, 1)) {
            return detailCompletions.getOrDefault(firstToken.getToken(), NO_COMPLETIONS).stream()
                    .filter( item -> item.getLabel().startsWith(tokens.get(1).getToken()))
                    .collect(Collectors.toList());
        }    


        return NO_COMPLETIONS;
    }

    private List<CompletionItem> completeViews(List<LineToken> tokens, CursorLocation cursor, C4DocumentModel docModel) {

        List<CompletionItem> completionIds = NO_COMPLETIONS;

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

        if(tokenizer.isBetweenTokens(cursor, 0, 1)) {
            return completionIds;
        }

        if(tokenizer.isInsideToken(cursor, 1)) {
            return completionIds.stream()
                    .filter( item -> item.getLabel().startsWith(tokens.get(1).getToken()))
                    .collect(Collectors.toList());
        }

        return NO_COMPLETIONS;
    }

    private List<CompletionItem> completionInViewIdentifiers(C4DocumentModel model, Predicate<C4ObjectWithContext<Element>> func) {
        return completionCreator.identifierCompletion( 
                model.getAllElements().stream()
                    .map(Entry::getValue)
                    .filter(element -> func.test(element))
                    .map(C4ObjectWithContext::getIdentifier)
                    .collect(Collectors.toList())
            );
    }

    List<String> getIdentifiers(C4DocumentModel model) {
        return model.getAllElements().stream()
                .map(ele -> ele.getValue().getIdentifier())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
