package de.systemticks.c4dsl.ls.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineToken {
    private String token;
    private int start;
    private int end;
}