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
    private AuthenticationManager authenticationManager; 

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
        user.setPassword("hashedPassword"); 
        user.setRole(Role.USER);
    }

    @Test
    @DisplayName("Register User - Success")
    void register_WhenEmailIsUnique_ShouldSaveAndReturnUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        when(userMapper.toModel(any(UserRegistrationRequestDto.class))).thenReturn(user);

        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        when(userRepository.save(any(User.class))).thenReturn(user);


        assertDoesNotThrow(() -> userService.register(registrationRequest));


        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register User - Email Already Exists")
    void register_WhenEmailExists_ShouldThrowRegistrationException() {

        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.of(user));

        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(registrationRequest)
        );

        assertEquals("User with email " + registrationRequest.getEmail() + " already exists.", exception.getMessage());
    }

    @Test
    @DisplayName("Login - Success with Valid Credentials")
    void login_WithValidCredentials_ShouldReturnJwt() {
        
        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));

        when(jwtService.generateToken(user)).thenReturn("dummy.jwt.token");

        String token = userService.login(loginRequest).getToken();


        verify(authenticationManager).authenticate(any());
        
        assertEquals("dummy.jwt.token", token);
    }

    @Test
    @DisplayName("Login - Failure with Invalid Credentials")
    void login_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
        
        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> userService.login(loginRequest)
        );
    }
}