package de.systemticks.c4dsl.ls.model;

import org.junit.jupiter.api.Test;

public class C4TokensLoaderTest {
    
    @Test
    public void load() {
        C4TokensLoader tokensLoader = new C4TokensLoader();
        tokensLoader.readConfiguration();
    }

}
