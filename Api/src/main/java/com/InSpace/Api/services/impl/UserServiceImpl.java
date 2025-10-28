package com.InSpace.Api.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.InSpace.Api.domain.Admin;
import com.InSpace.Api.domain.Role;
import com.InSpace.Api.domain.Token;
import com.InSpace.Api.domain.User;
import com.InSpace.Api.domain.enums.TokenType;
import com.InSpace.Api.infra.repository.AdminRepository;
import com.InSpace.Api.infra.repository.RoleRepository;
import com.InSpace.Api.infra.repository.UserRepository;
import com.InSpace.Api.infra.repository.TokenRepository;
import com.InSpace.Api.services.UserService;
import com.InSpace.Api.config.Security.JWTGenerator;
import com.InSpace.Api.services.dto.Auth.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AdminRepository adminRepository, AuthenticationManager authenticationManager, JWTGenerator jwtGenerator, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    @Override
    public AuthServiceResult registerUserAndSyncRole(RegisterRequestModel registerDto) {
        var result = new AuthServiceResult();
        try {

            if (registerDto.getUsername() == null || registerDto.getPassword() == null || registerDto.getEmail() == null
                    || registerDto.getRoleName() == null || registerDto.getUsername().isEmpty()
                    || registerDto.getPassword().isEmpty() || registerDto.getEmail().isEmpty()
                    || registerDto.getRoleName().isEmpty()) {
                throw new IllegalArgumentException("Missing Credentials");
            }

            if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username is already taken!");
            }
            User user = new User();
            user.setUsername(registerDto.getUsername());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email is already taken!");
            }
            user.setEmail(registerDto.getEmail());
            Role role = roleRepository.findByRoleName(registerDto.getRoleName());
            if (role == null) {
                throw new IllegalArgumentException("Invalid role!");
            }
            user.setRole(role);
            userRepository.save(user);
            boolean isSynced = false;
            String message;
            switch (registerDto.getRoleName().toUpperCase()) {
                case "ROLE_USER" -> message = "Registered as User";
                case "ROLE_ADMIN" -> {
                    isSynced = syncAdmin(user);
                    message = "Registered as Admin";
                }
                default -> throw new IllegalArgumentException("Unsupported role type!");
            }
            result.setMessage(message);
            result.setResultState(isSynced);
            return result;
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setResultState(false);
            return result;
        }
    }

    @Transactional
    public AccountResponse changePassword(AccountRePasswordModel accountData) {
        var response = new AccountResponse();
        // Step 2: Authenticate old password
        Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = oldAuthentication.getName();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            response.setMessage("User not found");
            return response;
        }

        User user = userOptional.get();
        boolean isPasswordCorrect = passwordEncoder.matches(accountData.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            response.setMessage("Incorrect password");
            return response;
        }

        // Step 3: Update the password in the database
        user.setPassword(passwordEncoder.encode(accountData.getNewPassword()));
        userRepository.save(user);

        // Step 4: Generate new authentication token
        SecurityContextHolder.clearContext();
        Authentication newAuthentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        accountData.getNewPassword()));
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        String newToken = jwtGenerator.generateToken(newAuthentication);
        response.setToken(newToken);
        response.setMessage("Password updated successfully");
        response.setResultState(true);
        return response;
    }

    @Transactional
    public AccountResponse changeUsername(AccountReUsernameModel accountData) {

        var result = new AccountResponse();

        var oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        oldAuthentication.getName(),
                        accountData.getPassword()));

        Optional<User> userOptional = userRepository.findByUsername(accountData.getUsername());
        if (userOptional.isEmpty()) {
            result.setMessage("User not found");
            result.setResultState(false);
            return result;
        }

        Optional<User> user = userRepository.findByUsername(accountData.getNewUsername());
        if (user.isPresent()) {
            result.setMessage("Username is already taken!");
            result.setResultState(false);
            return result;
        }

        User oldUser = userOptional.get();
        User newUser = User.builder()
                .userId(oldUser.getUserId())
                .username(accountData.getNewUsername())
                .password(oldUser.getPassword())
                .email(oldUser.getEmail())
                .role(oldUser.getRole())
                .build();
        userRepository.save(newUser);

        SecurityContextHolder.clearContext();
        Authentication newAuthentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        accountData.getNewUsername(),
                        accountData.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        String newToken = jwtGenerator.generateToken(newAuthentication);
        result.setToken(newToken);
        result.setMessage("username changed Successfully");
        result.setResultState(true);
        return result;
    }

    @Transactional
    @Override
    public AccountResponse changeMail(AccountReMailModel accountData) {
        // Step 2: Authenticate password
        String username = jwtGenerator.getUsernameFromJWT(accountData.getToken());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        accountData.getPassword()));

        var result = new AccountResponse();
        Optional<User> userOptional = userRepository.findByUsername(accountData.getUsername());
        if (userOptional.isEmpty()) {
            result.setMessage("User not found");
            result.setResultState(false);
            return result;
        }

        if (userRepository.findByEmail(accountData.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already taken!");
        }

        User user = userOptional.get();
        user.setEmail(accountData.getEmail());
        userRepository.save(user);

        result.setMessage("OK");
        result.setResultState(false);
        return result;
    }

    private boolean syncAdmin(User user) {
        try {
            Admin admin = new Admin();
            admin.setUser(user);
            // admin.setAccessLevel(registerDto.getAccessLevel());
            adminRepository.save(admin);
            return true;
        } catch (Exception e) {
            System.err.println("Error syncing admin: " + e.getMessage());
            return false;
        }
    }

    public Long getUserIdFromUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

	@Override
	public LoginResponse loginUser(LoginRequestModel loginDto) {

        Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
            loginDto.getUsername(),
            loginDto.getPassword()
          ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        var usr = userRepository.findById(getUserIdFromUsername(loginDto.getUsername()));
        revokeAllUserTokens(usr.get()); // keep only one valid token
        saveUserToken(usr.get(), token);

        var loginResponse = new LoginResponse(token);
        loginResponse.setMessage("login successfully");
        loginResponse.setState(true);
        
        return loginResponse;
	}

    private void saveUserToken(User user, String jwtToken) {
        var toSaveToken = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .revoked(false)
            .expired(false)
            .build();
        tokenRepository.save(toSaveToken);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        for (var token : validUserTokens) {
            token.setExpired(true);
            token.setRevoked(true);
        }
        tokenRepository.saveAll(validUserTokens);
    } 
}