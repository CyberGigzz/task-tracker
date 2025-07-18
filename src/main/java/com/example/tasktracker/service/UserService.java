package com.example.tasktracker.service;

import com.example.tasktracker.dto.user.UserLoginRequestDto;
import com.example.tasktracker.dto.user.UserLoginResponseDto;
import com.example.tasktracker.dto.user.UserRegistrationRequestDto;
import com.example.tasktracker.dto.user.UserResponseDto;

public interface UserService {
    
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    UserLoginResponseDto login(UserLoginRequestDto requestDto);
}