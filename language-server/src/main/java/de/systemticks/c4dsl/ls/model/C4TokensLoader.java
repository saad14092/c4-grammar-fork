package de.systemticks.c4dsl.ls.model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class C4TokensLoader {
 
    private final static String TOKEN_CONFIG = "config/c4tokens.json";
    private Gson gson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(C4TokensLoader.class);    

    public C4TokensConfig readConfiguration() {

        InputStream configIs = this.getClass().getClassLoader().getResourceAsStream(TOKEN_CONFIG);
        Reader reader;
        C4TokensConfig config = null;

        try {
            reader = new InputStreamReader(configIs, "UTF-8");
            config = gson.fromJson(reader, C4TokensConfig.class);
        } catch (UnsupportedEncodingException e) {
            logger.error("Loading token configration failed: {}", e.getMessage());
        }

        return config;
    }

}
