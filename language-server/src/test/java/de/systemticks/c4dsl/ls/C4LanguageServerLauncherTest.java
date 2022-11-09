package de.systemticks.c4dsl.ls;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

public class C4LanguageServerLauncherTest {
    
   // @Test
    public void testProcessIOConnection() {

        try {
            Process process = Runtime.getRuntime().exec("../extension/server/c4-language-server/bin/c4-language-server");

            assertTrue(process.isAlive());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
