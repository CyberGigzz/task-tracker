package com.example.tasktracker.service.impl;

import com.example.tasktracker.dto.user.UserLoginRequestDto;
import com.example.tasktracker.dto.user.UserRegistrationRequestDto;
import com.example.tasktracker.exception.RegistrationException;
import com.example.tasktracker.mapper.UserMapper;
import com.example.tasktracker.model.Role;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.UserRepository;
import com.example.tasktracker.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager; // Mock the AuthenticationManager

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequestDto registrationRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequestDto();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setRole(Role.USER);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword"); // Assume it's hashed
        user.setRole(Role.USER);
    }

    @Test
    @DisplayName("Register User - Success")
    void register_WhenEmailIsUnique_ShouldSaveAndReturnUser() {
        // Arrange
        // 1. When checking if the user exists, return empty (it's a new user).
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // 2. When the mapper converts DTO to model, return our user object.
        when(userMapper.toModel(any(UserRegistrationRequestDto.class))).thenReturn(user);
        // 3. When the password encoder is called, return a dummy hashed password.
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        // 4. When the user is saved, return the user object.
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        // We use assertDoesNotThrow because a successful registration should not throw an exception.
        assertDoesNotThrow(() -> userService.register(registrationRequest));

        // Assert
        // Verify that the save method was indeed called on the repository.
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register User - Email Already Exists")
    void register_WhenEmailExists_ShouldThrowRegistrationException() {
        // Arrange
        // When checking for the user, return our existing user object.
        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        // Check that calling register now throws the correct exception.
        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(registrationRequest)
        );

        assertEquals("User with email " + registrationRequest.getEmail() + " already exists.", exception.getMessage());
    }

    @Test
    @DisplayName("Login - Success with Valid Credentials")
    void login_WithValidCredentials_ShouldReturnJwt() {
        // Arrange
        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // When the user is searched by email, return our user.
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        // When the JWT service generates a token, return a dummy token.
        when(jwtService.generateToken(user)).thenReturn("dummy.jwt.token");

        // Act
        String token = userService.login(loginRequest).getToken();

        // Assert
        // 1. Verify that the authenticationManager.authenticate() method was called. This is crucial.
        verify(authenticationManager).authenticate(any());
        // 2. Check that the returned token is the one we mocked.
        assertEquals("dummy.jwt.token", token);
    }

    @Test
    @DisplayName("Login - Failure with Invalid Credentials")
    void login_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
        // Arrange
        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // The key part: when authenticate is called, we tell it to throw an exception,
        // just like Spring Security would do for a real failed login.
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        // We check that the login method re-throws the exception from the AuthenticationManager.
        assertThrows(
                BadCredentialsException.class,
                () -> userService.login(loginRequest)
        );
    }
}