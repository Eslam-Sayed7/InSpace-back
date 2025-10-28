package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.infra.repository.RoleRepository;
import com.InSpace.Api.infra.repository.UserRepository;
// import com.InSpace.Api.services.EmailService;
import com.InSpace.Api.services.UserService;
import com.InSpace.Api.services.dto.Auth.AuthServiceResult;
import com.InSpace.Api.services.dto.Auth.LoginRequestModel;
import com.InSpace.Api.services.dto.Auth.RegisterRequestModel;
import com.InSpace.Api.services.dto.Email.EmailFormateDto;

import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public AuthController(UserRepository userRepository,
            RoleRepository roleRepository, PasswordEncoder passwordEncoder,UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @PostMapping("login")
    public ResponseEntity<AuthServiceResult> login(@RequestBody LoginRequestModel loginDto) {

        AuthServiceResult result = new AuthServiceResult();

        if (isMissingCredentials(loginDto)) {
            return buildErrorResponse(result, "Missing Credentials");
        }

        try {
            result = userService.loginUser(loginDto);

            if (!result.isResultState()) {
                return buildErrorResponse(result, result.getMessage());
            }
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return buildErrorResponse(result, "Wrong Credentials");
        }
    }

    @Transactional
    @PostMapping("register")
    public ResponseEntity<AuthServiceResult> register(@RequestBody RegisterRequestModel registerDto) {

        AuthServiceResult result = new AuthServiceResult();

        try {
            result = userService.registerUserAndSyncRole(registerDto);
            if (result.isResultState()) {
                
                var email = new EmailFormateDto();
                email.setTo(registerDto.getEmail());
                email.setSubject("Registration");
                email.setEmailBody("You have successfully registered");
                // emailService.sendEmail(email); // uncomment if you have the right yml file with the email service crendtial

                result.setMessage("Registered successfully");
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return buildErrorResponse(result, result.getMessage());
    }

    private boolean isMissingCredentials(LoginRequestModel loginDto) {
        return loginDto == null
                || loginDto.getUsername() == null
                || loginDto.getPassword() == null
                || loginDto.getUsername().isEmpty()
                || loginDto.getPassword().isEmpty();
    }

    private ResponseEntity<AuthServiceResult> buildErrorResponse(AuthServiceResult responseEntity, String message) {
        if(responseEntity == null){
            responseEntity = new AuthServiceResult();
        }
        responseEntity.setMessage(message);
        return new ResponseEntity<>(responseEntity, HttpStatus.BAD_REQUEST);
    }
}
