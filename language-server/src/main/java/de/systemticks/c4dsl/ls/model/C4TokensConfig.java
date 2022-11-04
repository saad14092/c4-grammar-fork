package de.systemticks.c4dsl.ls.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class C4TokensConfig {
    private List<C4TokenScope> scopes;
    private List<C4TokenDetail> details;

    // for test purposes
    public void addScope(String name, List<String> keywords, boolean hasRelations) {
        if(scopes == null) {
            scopes = new ArrayList<>();
        }
        scopes.add(new C4TokenScope(name, keywords, hasRelations));
    }

    // for test purposes
    public void addDetail(String keyword, List<String> choices) {
        if(details == null) {
            details = new ArrayList<>();
        }
        details.add(new C4TokenDetail(keyword, choices));
    }
    
    @Data
    @AllArgsConstructor
    public class C4TokenScope {
        private String name;
        private List<String> keywords;
        @SerializedName("hasRelations")
        private boolean relations;        
    }    

    @Data
    @AllArgsConstructor
    public class C4TokenDetail {
        private String keyword;
        @SerializedName("choice")
        private List<String> choices;
    }
}

