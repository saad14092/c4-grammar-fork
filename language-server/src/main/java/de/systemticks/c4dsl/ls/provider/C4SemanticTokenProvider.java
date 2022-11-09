package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.structurizr.model.ContainerInstance;
import com.structurizr.model.Element;
import com.structurizr.model.SoftwareSystemInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;
import de.systemticks.c4dsl.ls.model.C4ObjectWithContext;
import de.systemticks.c4dsl.ls.utils.C4Utils;

public class C4SemanticTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(C4SemanticTokenProvider.class);

    public static final List<String> TOKEN_TYPES = Arrays.asList(
        "comment", "string", "keyword", "number", "regexp", "operator", "namespace",
        "type", "struct", "class", "interface", "enum", "typeParameter", "function",
        "member", "macro", "variable", "parameter", "property", "label", "event");
    
    public static final List<String> TOKEN_MODIFIERS = Arrays.asList(
        "declaration", "definition", "readonly", "static", "deprecated",
        "abstract", "async", "modification", "documentation", "defaultLibrary");
    
    private final static int MODEL_ELEMENT = TOKEN_TYPES.indexOf("member");

    public List<Integer> calculateTokens(C4DocumentModel c4Model) {

        List<C4SemanticToken> tokens = new ArrayList<>();

        tokens.addAll(c4Model.getAllViews().stream()
            .map( entry -> createToken(C4Utils.getIdentifierOfView(entry.getValue()), c4Model, entry.getKey()-1))
            .filter(element -> element != null).collect(Collectors.toList()));

        tokens.addAll(c4Model.getAllRelationships().stream()
            .map(entry -> createToken(entry.getValue().getObject().getSourceId(), c4Model, entry.getKey()-1))
            .filter(element -> element != null).collect(Collectors.toList()));

        tokens.addAll(c4Model.getAllRelationships().stream()
            .map(entry -> createToken(entry.getValue().getObject().getDestinationId(), c4Model, entry.getKey()-1))
            .filter(element -> element != null).collect(Collectors.toList()));

        c4Model.getAllElements().forEach( e -> {
            Element element = e.getValue().getObject();
            if(element instanceof ContainerInstance) {
                C4SemanticToken token = createToken( ((ContainerInstance)element).getContainerId(), c4Model, e.getKey()-1);
                if(token != null) {
                    tokens.add(token);
                }
            }
            else if(element instanceof SoftwareSystemInstance) {
                C4SemanticToken token = createToken( ((SoftwareSystemInstance)element).getSoftwareSystemId(), c4Model, e.getKey()-1);
                if(token != null) {
                    tokens.add(token);
                }
            }
        });

        return buildTokensAsList(tokens);

    }

    private C4SemanticToken createToken(String referenceId, C4DocumentModel c4Model, int line) {
        List<Entry<Integer, C4ObjectWithContext<Element>>> elements = c4Model.findElementsById(referenceId);
        if(elements.size() == 1) {
            String identifier = elements.get(0).getValue().getIdentifier();
            if(identifier != null) {
                final int startPos = C4Utils.getStartPosition(c4Model.getLineAt(line), identifier);
                return new C4SemanticToken(line, startPos, identifier.length(), MODEL_ELEMENT, 0);
            }
        }
        return null;
    }

    private List<Integer> buildTokensAsList(List<C4SemanticToken> tokens) {
        
        // https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#textDocument_semanticTokens
        List<Integer> result = new ArrayList<>();

        List<C4SemanticToken> sorted = tokens.stream().sorted(Comparator.comparing(C4SemanticToken::getLineNumber)).collect(Collectors.toList());

        for(int index= 0; index<sorted.size(); index++) {
            C4SemanticToken token = sorted.get(index);
            if(index == 0) {
                result.add(token.getLineNumber());
                result.add(token.getStartPos());
            }
            else {
                C4SemanticToken predecessor = sorted.get(index-1);
                final int deltaLine = token.getLineNumber() - predecessor.getLineNumber();
                final int deltaChar = deltaLine == 0 ? token.getStartPos() - predecessor.getStartPos() : token.getStartPos();
                result.add(deltaLine);
                result.add(deltaChar);
            }
            result.add(token.getLength());
            result.add(token.getTokenType());
            result.add(token.getTokenModifier());
        }

        logger.debug("Semantik Tokens at {}", result);

        return result;
    }

}