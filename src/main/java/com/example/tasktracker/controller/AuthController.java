package com.example.tasktracker.controller;

import com.example.tasktracker.dto.user.UserLoginRequestDto;
import com.example.tasktracker.dto.user.UserLoginResponseDto;
import com.example.tasktracker.dto.user.UserRegistrationRequestDto;
import com.example.tasktracker.dto.user.UserResponseDto;
import com.example.tasktracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication Management", description = "Endpoints for user registration and login")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserRegistrationRequestDto requestDto) {
        UserResponseDto responseDto = userService.register(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in a user", description = "Authenticates a user and returns a JWT")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        UserLoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}