package com.InSpace.Api;

import com.InSpace.Api.adapters.controller.UserController;
import com.InSpace.Api.services.UserService;
import com.InSpace.Api.services.dto.Auth.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testChangePassword_Success() {
        AccountRePasswordModel accountData = new AccountRePasswordModel();
        AccountResponse mockResponse = new AccountResponse();
        mockResponse.setResultState(true);
        mockResponse.setMessage("Password changed successfully");

        when(userService.changePassword(accountData)).thenReturn(mockResponse);

        ResponseEntity<AccountResponse> response = userController.changePassword(accountData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResultState());
        assertEquals("Password changed successfully", response.getBody().getMessage());
    }

    @Test
    void testChangePassword_Failure() {
        AccountRePasswordModel accountData = new AccountRePasswordModel();
        AccountResponse mockResponse = new AccountResponse();
        mockResponse.setResultState(false);
        mockResponse.setMessage("Error changing password");

        when(userService.changePassword(accountData)).thenThrow(new RuntimeException("Error changing password"));

        ResponseEntity<AccountResponse> response = userController.changePassword(accountData);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error changing password", response.getBody().getMessage());
    }

    @Test
    void testChangeEmail_Success() {
        AccountReMailModel accountData = new AccountReMailModel();
        AccountResponse mockResponse = new AccountResponse();
        mockResponse.setResultState(true);
        mockResponse.setMessage("Email changed successfully");

        when(userService.changeMail(accountData)).thenReturn(mockResponse);

        ResponseEntity<AccountResponse> response = userController.changemail(accountData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResultState());
        assertEquals("Email changed successfully", response.getBody().getMessage());
    }

    @Test
    void testChangeEmail_Failure() {
        AccountReMailModel accountData = new AccountReMailModel();

        when(userService.changeMail(accountData)).thenThrow(new RuntimeException("Error changing email"));

        ResponseEntity<AccountResponse> response = userController.changemail(accountData);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error changing email", response.getBody().getMessage());
    }

    @Test
    void testChangeUsername_Success() {
        AccountReUsernameModel accountData = new AccountReUsernameModel();
        AccountResponse mockResponse = new AccountResponse();
        mockResponse.setResultState(true);
        mockResponse.setMessage("Username changed successfully");

        when(userService.changeUsername(accountData)).thenReturn(mockResponse);

        ResponseEntity<AccountResponse> response = userController.changeUsername(accountData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResultState());
        assertEquals("Username changed successfully", response.getBody().getMessage());
    }

    @Test
    void testChangeUsername_Failure() {
        AccountReUsernameModel accountData = new AccountReUsernameModel();

        when(userService.changeUsername(accountData)).thenThrow(new RuntimeException("Error changing username"));

        ResponseEntity<AccountResponse> response = userController.changeUsername(accountData);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error changing username", response.getBody().getMessage());
    }
}
