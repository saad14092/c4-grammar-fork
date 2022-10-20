package de.systemticks.c4dsl.ls.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class C4TokensConfig {
    private List<C4TokenScope> scopes;

    @Data
    public class C4TokenScope {
        private String name;
        private List<String> keywords;
        @SerializedName("hasRelations")
        private boolean relations;
    }    
}

