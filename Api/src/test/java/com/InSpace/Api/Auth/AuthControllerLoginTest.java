package com.InSpace.Api.Auth;

import com.InSpace.Api.adapters.controller.AuthController;
import com.InSpace.Api.config.Security.JWTGenerator;
import com.InSpace.Api.infra.repository.RoleRepository;
import com.InSpace.Api.infra.repository.UserRepository;
import com.InSpace.Api.services.EmailService;
import com.InSpace.Api.services.UserService;
import com.InSpace.Api.services.dto.Auth.AuthServiceResult;
import com.InSpace.Api.services.dto.Auth.LoginRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerLoginTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTGenerator jwtGenerator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Successful() {
        // Arrange
        LoginRequestModel loginRequest = new LoginRequestModel();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    // Stub the service used by the controller
    AuthServiceResult authServiceResult = new AuthServiceResult();
    authServiceResult.setMessage("login successfully");
    authServiceResult.setResultState(true);
    authServiceResult.setToken("test-token");

    when(userService.loginUser(any(LoginRequestModel.class))).thenReturn(authServiceResult);

        // Act
        var response = authController.login(loginRequest);

    // Assert
    assertEquals(200, response.getStatusCode().value());
    AuthServiceResult body = (AuthServiceResult) response.getBody();
    assertEquals("test-token", body.getToken());

    verify(userService, times(1)).loginUser(any(LoginRequestModel.class));
    }

    @Test
    void login_MissingCredentials() {
        // Arrange
        LoginRequestModel loginRequest = new LoginRequestModel();
        loginRequest.setUsername("");
        loginRequest.setPassword("");
        // Act
        var response = authController.login(loginRequest);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        var responseBody = (AuthServiceResult) response.getBody();
        String errorMessage = responseBody.getMessage();

        assertEquals("Missing Credentials", errorMessage);

        verifyNoInteractions(userService, authenticationManager, jwtGenerator);
    }

    @Test
    void login_InvalidCredentials() {
        // Arrange
        LoginRequestModel loginRequest = new LoginRequestModel();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("wrongpassword");
    when(userService.loginUser(any(LoginRequestModel.class)))
        .thenThrow(new RuntimeException("Authentication failed"));

    // Act
    var response = authController.login(loginRequest);

    // Assert
    assertEquals(400, response.getStatusCode().value());
    var responseBody =  response.getBody();
    String errorMessage = responseBody.getMessage();

    assertEquals("Wrong Credentials", errorMessage);

    verify(userService, times(1)).loginUser(any(LoginRequestModel.class));
    verifyNoInteractions(jwtGenerator, authenticationManager);
    }
}
