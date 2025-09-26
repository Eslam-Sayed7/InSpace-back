package com.InSpace.Api.Auth;

import com.InSpace.Api.adapters.controller.AuthController;
import com.InSpace.Api.config.Security.JWTGenerator;
import com.InSpace.Api.infra.repository.RoleRepository;
import com.InSpace.Api.infra.repository.UserRepository;
import com.InSpace.Api.services.EmailService;
import com.InSpace.Api.services.UserService;
import com.InSpace.Api.services.dto.Auth.AuthServiceResult;
import com.InSpace.Api.services.dto.Auth.RegisterRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AuthControllerRegisterTest {

        private MockMvc mockMvc;
        private UserService userService;
        // private EmailService emailService;
        private AuthController authController;
        private ObjectMapper objectMapper;
        private AuthenticationManager authenticationManager;
        private UserRepository userRepository;
        private RoleRepository roleRepository;
        private PasswordEncoder passwordEncoder;
        private JWTGenerator jwtGenerator;

        @BeforeEach
        void setUp() {
                // Create mock services
                userService = mock(UserService.class);
                // emailService = mock(EmailService.class);

                // Create the controller with mock services
                authController = new AuthController(authenticationManager, userRepository,
                                roleRepository, passwordEncoder, jwtGenerator, userService);

                // Setup MockMvc
                mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

                // Initialize ObjectMapper
                objectMapper = new ObjectMapper();
        }

        private RegisterRequestModel createValidRegisterDto(String role) {
                var registerDto = new RegisterRequestModel();
                registerDto.setUsername("testuser");
                registerDto.setPassword("testpassword");
                registerDto.setEmail("testuser@example.com");
                registerDto.setRoleName(role);
                return registerDto;
        }

        @Test
        void testRegisterUser_Success() throws Exception {
                // Arrange
                RegisterRequestModel UsereRegisterDto = createValidRegisterDto("ROLE_USER");

                // Create a successful AuthServiceResult
                AuthServiceResult successResult = new AuthServiceResult();
                successResult.setMessage("Registered successfully");
                successResult.setResultState(true);

                // Mock service method behavior
                when(userService.registerUserAndSyncRole(any(RegisterRequestModel.class)))
                                .thenReturn(successResult);

                // Act and Assert
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(UsereRegisterDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Registered successfully"))
                                .andExpect(jsonPath("$.resultState").value(true));

        }

        @Test
        void testRegisterUser_Failure() throws Exception {
                // Arrange
                RegisterRequestModel registerDto = new RegisterRequestModel();
                registerDto.setUsername("existinguser");
                registerDto.setPassword("testpassword");
                registerDto.setEmail("existinguser@example.com");
                registerDto.setRoleName("ROLE_USER");

                // Create a failed AuthServiceResult
                AuthServiceResult failureResult = new AuthServiceResult();
                failureResult.setMessage("Username is already taken!");
                failureResult.setResultState(false);

                // Mock service method behavior
                when(userService.registerUserAndSyncRole(any(RegisterRequestModel.class)))
                                .thenReturn(failureResult);

                // Act and Assert
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Username is already taken!"))
                                .andExpect(jsonPath("$.resultState").value(false));

                // Verify interactions
                verify(userService, times(1)).registerUserAndSyncRole(any(RegisterRequestModel.class));
        }
}
