package com.InSpace.Api.services.dto.Auth;

public class AuthServiceResult {
    private String Message;
    private boolean ResultState;
    private String Token;

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

    public String getToken() {
        return Token;
    }

    public void setToken(String Token) {
        this.Token = Token;
    }
}
