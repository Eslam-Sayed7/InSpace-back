package com.InSpace.Api.services.dto.ai;

public class GenerateRequest {

    private String schemaKey;
    private boolean stream;
    private String message;
    private String model;

    public GenerateRequest() {
    }

    public GenerateRequest(String schemaKey, boolean stream, String message, String model) {
        this.schemaKey = schemaKey;
        this.stream = stream;
        this.message = message;
        this.model = model;
    }


    public String getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }
}
