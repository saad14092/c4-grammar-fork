package de.systemticks.c4dsl.ls.model;

public class C4ObjectWithContext<T> {
    
    private String identifier;
    private T object;
    private C4DocumentModel container;

    public C4ObjectWithContext(String identifier, T object, C4DocumentModel container) {
        this.identifier = identifier;
        this.object = object;
        this.container = container;
    }

    public String getIdentifier() {
        return identifier;
    }

    public T getObject() {
        return object;
    }

    public C4DocumentModel getContainer() {
        return container;
    }

}
