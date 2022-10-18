package de.systemticks.c4dsl.ls.model;

import lombok.Data;

@Data
public class C4CompletionScope {

    final public static int SCOPE_NOT_CLOSED = -1;

    private int id;
    private int startsAt;
    private int endsAt;
    private String name;

    public C4CompletionScope(int id, String name, int start) {
        this.id = id;
        this.startsAt = start;
        this.name = name;
        this.endsAt = SCOPE_NOT_CLOSED;
    }
}
