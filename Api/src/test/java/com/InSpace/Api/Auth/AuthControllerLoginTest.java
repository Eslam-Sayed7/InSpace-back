package com.InSpace.Api.Auth;

import com.InSpace.Api.adapters.controller.AuthController;
import com.InSpace.Api.config.Security.JWTGenerator;
import com.InSpace.Api.infra.repository.RoleRepository;
import com.InSpace.Api.infra.repository.UserRepository;
import com.InSpace.Api.services.EmailService;
import com.InSpace.Api.services.UserService;
import com.InSpace.Api.services.dto.Auth.LoginRequestModel;
import com.InSpace.Api.services.dto.Auth.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

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

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtGenerator.generateToken(authentication)).thenReturn("test-token");

        // Act
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test-token", response.getBody().getToken());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtGenerator, times(1)).generateToken(authentication);
    }

    @Test
    void login_MissingCredentials() {
        // Arrange
        LoginRequestModel loginRequest = new LoginRequestModel();
        loginRequest.setUsername("");
        loginRequest.setPassword("");

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        String errorMessage = responseBody.get("error");

        assertEquals("Missing Credentials", errorMessage);

        verifyNoInteractions(authenticationManager, jwtGenerator);
    }

    @Test
    void login_InvalidCredentials() {
        // Arrange
        LoginRequestModel loginRequest = new LoginRequestModel();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        String errorMessage = responseBody.get("error");

        assertEquals("Wrong Credentials", errorMessage);

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtGenerator);
    }
}
