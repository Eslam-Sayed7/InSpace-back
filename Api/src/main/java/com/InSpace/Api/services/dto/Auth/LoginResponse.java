package com.InSpace.Api.services.dto.Auth;

public class LoginResponse {

    private String Token;

    public LoginResponse(String token) {
        Token = token;
    }

    public String getToken() {
        return Token;
    }
}
