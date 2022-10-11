package de.systemticks.c4dsl.ls.commands;

public class C4ExportPlantUmlDto {

    private String uri;
    private String renderer;
    private String outDir;

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getRenderer() {
        return renderer;
    }
    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }
    public String getOutDir() {
        return outDir;
    }
    public void setExportDir(String outDir) {
        this.outDir = outDir;
    }

}
