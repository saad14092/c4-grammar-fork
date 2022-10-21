package de.systemticks.c4dsl.ls.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class C4TokensConfig {
    private List<C4TokenScope> scopes;

    // for test purposes
    public void addScope(String name, List<String> keywords, boolean hasRelations) {
        if(scopes == null) {
            scopes = new ArrayList<>();
        }
        scopes.add(new C4TokenScope(name, keywords, hasRelations));
    }
    
    @Data
    @AllArgsConstructor
    public class C4TokenScope {
        private String name;
        private List<String> keywords;
        @SerializedName("hasRelations")
        private boolean relations;
    }    
}

