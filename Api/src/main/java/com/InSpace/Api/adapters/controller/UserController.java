package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.config.Security.JWTGenerator;
import com.InSpace.Api.infra.repository.RoleRepository;
import com.InSpace.Api.infra.repository.UserRepository;
import com.InSpace.Api.services.EmailService;
import com.InSpace.Api.services.UserService;
import com.InSpace.Api.services.dto.Auth.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, UserRepository userRepository,
            EmailService emailService, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
            JWTGenerator jwtGenerator, UserService userService) {
        this.userService = userService;
    }

    @PostMapping("password-change")
    public ResponseEntity<AccountResponse> changePassword(@RequestBody AccountRePasswordModel accountData) {

        AccountResponse result = new AccountResponse();
        try {
            result = userService.changePassword(accountData);

            result.setResultState(result.isResultState());
            result.setMessage(result.getMessage());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PermitAll
    @PostMapping("email-change")
    public ResponseEntity<AccountResponse> changemail(@RequestBody AccountReMailModel accountData) {

        AccountResponse result = new AccountResponse();
        try {
            // accountData.setUsername(username);
            result = userService.changeMail(accountData);
            return new ResponseEntity<AccountResponse>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            return new ResponseEntity<AccountResponse>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PermitAll
    @PostMapping("username-change")
    public ResponseEntity<AccountResponse> changeUsername(@RequestBody AccountReUsernameModel accountData) {

        AccountResponse result = new AccountResponse();
        try {
            result = userService.changeUsername(accountData);
            return new ResponseEntity<AccountResponse>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            return new ResponseEntity<AccountResponse>(result, HttpStatus.BAD_REQUEST);
        }
    }
}