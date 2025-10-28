package com.InSpace.Api.services.dto.Auth;

public class LoginResponse {

    private String Token;
    private String Message;
	  private boolean State;

    public void setToken(String token) {
		  Token = token;
	  }

	  public String getMessage() {
		  return Message;
	  }

	  public void setMessage(String message) {
		  Message = message;
	  }

	  public boolean isState() {
		  return State;
	  }

	  public void setState(boolean state) {
		  State = state;
	  }


    public LoginResponse(String token) {
        Token = token;
    }

    public String getToken() {
        return Token;
    }
}
