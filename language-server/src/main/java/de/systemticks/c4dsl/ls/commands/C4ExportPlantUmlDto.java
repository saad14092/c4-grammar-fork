package de.systemticks.c4dsl.ls.commands;

import lombok.Data;

@Data
public class C4ExportPlantUmlDto {

    private String uri;
    private String renderer;
    private String outDir;
    
}
