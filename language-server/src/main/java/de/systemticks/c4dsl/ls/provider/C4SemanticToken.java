package de.systemticks.c4dsl.ls.provider;

public class C4SemanticToken {
    
    private int lineNumber;
    private int startPos;
    private int length;
    private int tokenType;
    private int tokenModifier;

    public C4SemanticToken(int lineNumber, int startPos, int length, int tokenType, int tokenModifier) {
        this.lineNumber = lineNumber;
        this.startPos = startPos;
        this.length = length;
        this.tokenType = tokenType;
        this.tokenModifier = tokenModifier;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getTokenType() {
        return tokenType;
    }

    public int getTokenModifier() {
        return tokenModifier;
    }

    public int getLength() {
        return length;
    }

}
