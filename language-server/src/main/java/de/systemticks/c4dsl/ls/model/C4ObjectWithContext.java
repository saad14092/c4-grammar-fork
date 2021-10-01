package de.systemticks.c4dsl.ls.model;

public class C4ObjectWithContext<T> {
    
    private String identifier;
    private T object;
    private int endLine;

    public C4ObjectWithContext(String identifier, T object) {
        this.identifier = identifier;
        this.object = object;
        this.endLine = -1;
    }

    public C4ObjectWithContext(String identifier, T object, int endLine) {
        this.identifier = identifier;
        this.object = object;
        this.endLine = endLine;
    }

    public String getIdentifier() {
        return identifier;
    }

    public T getObject() {
        return object;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }


}
