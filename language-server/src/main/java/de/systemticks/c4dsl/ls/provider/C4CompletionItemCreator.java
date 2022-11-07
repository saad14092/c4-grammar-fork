package de.systemticks.c4dsl.ls.provider;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

import de.systemticks.c4dsl.ls.model.C4TokensConfig.C4TokenSnippet;

public class C4CompletionItemCreator {

    public List<CompletionItem> keyWordCompletion(List<String> keywords) {
        return keywords.stream().map(keyword -> createCompletionItem(keyword, CompletionItemKind.Keyword))
                .collect(Collectors.toList());
    }

    public List<CompletionItem> propertyCompletion(List<String> properties) {
        return properties.stream().map(prop -> createCompletionItem(prop, CompletionItemKind.Property))
                .collect(Collectors.toList());
    }

    public List<CompletionItem> snippetCompletion(List<C4TokenSnippet> snippets) {
        return snippets.stream().map(snippet -> {
            CompletionItem item = createCompletionItem(snippet.getLabel(), CompletionItemKind.Snippet);
            item.setDetail(snippet.getDetail());
            item.setInsertTextFormat(InsertTextFormat.Snippet);
            item.setInsertText(snippet.getInsertText());
            return item;
        }).collect(Collectors.toList());
    }

    public List<CompletionItem> identifierCompletion(List<String> identifier) {
        return identifier.stream().map(id -> createCompletionItem(id, CompletionItemKind.Reference))
                .collect(Collectors.toList());
    }

    private CompletionItem createCompletionItem(String label, CompletionItemKind kind) {
        CompletionItem item = new CompletionItem();
        item.setLabel(label);
        item.setKind(kind);
        return item;
    }

}
