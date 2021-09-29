package de.systemticks.c4dsl.ls.model;

public class C4WithId<T> {
    
    private String identifier;
    private T object;

    public C4WithId(String identifier, T object) {
        this.identifier = identifier;
        this.object = object;
    }

    public String getIdentifier() {
        return identifier;
    }

    public T getObject() {
        return object;
    }

}
