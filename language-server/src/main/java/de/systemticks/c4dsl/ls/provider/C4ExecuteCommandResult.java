package de.systemticks.c4dsl.ls.provider;

import com.google.gson.JsonObject;

public enum C4ExecuteCommandResult {
    
    UNKNOWN_COMMAND (0),
    ILLEGAL_ARGUMENTS (1),
    STRUCTURIZR_PARSER_EXCEPTION (2),
    IO_EXCEPTION (3),
    UNKNOWN_FAILURE (99, "Unknown Failure"),
    OK (100);

    private int resultCode;

    private String message;

    C4ExecuteCommandResult(int resultCode) {
        this(resultCode, "");
    }

    C4ExecuteCommandResult(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public C4ExecuteCommandResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public JsonObject toJson() {
        JsonObject jObj = new JsonObject();
        jObj.addProperty("resultcode", this.resultCode);
        jObj.addProperty("message", this.getMessage());
        return jObj;
    }

}

