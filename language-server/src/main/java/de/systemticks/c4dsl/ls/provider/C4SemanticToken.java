package de.systemticks.c4dsl.ls.provider;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class C4SemanticToken {
    
    private int lineNumber;
    private int startPos;
    private int length;
    private int tokenType;
    private int tokenModifier;

}
