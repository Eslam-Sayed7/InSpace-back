package com.InSpace.Api.services.dto;

public class SuccessResponse {
    private String message;
    private long timestamp;

    public SuccessResponse(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}