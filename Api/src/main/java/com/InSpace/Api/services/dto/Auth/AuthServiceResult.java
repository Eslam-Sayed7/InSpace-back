package com.InSpace.Api.services.dto.Auth;

public class AuthServiceResult {
    private String Message;
    private boolean ResultState;

    public void setMessage(String message) {
        Message = message;
    }

    public String getMessage() {
        return Message;
    }

    public boolean isResultState() {
        return ResultState;
    }

    public void setResultState(boolean resultState) {
        ResultState = resultState;
    }
}
