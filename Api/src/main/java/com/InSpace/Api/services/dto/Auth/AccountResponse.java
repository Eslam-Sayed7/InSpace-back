package com.InSpace.Api.services.dto.Auth;

public class AccountResponse {
    private String Token;
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

    public String gettoken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
