package com.example.tasktracker.service.impl;

import com.example.tasktracker.dto.user.UserLoginRequestDto;
import com.example.tasktracker.dto.user.UserLoginResponseDto;
import com.example.tasktracker.dto.user.UserRegistrationRequestDto;
import com.example.tasktracker.dto.user.UserResponseDto;
import com.example.tasktracker.exception.RegistrationException;
import com.example.tasktracker.mapper.UserMapper;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.UserRepository;
import com.example.tasktracker.security.JwtService;
import com.example.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with email " + requestDto.getEmail() + " already exists.");
        }

        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
        );

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication")); 

        String jwtToken = jwtService.generateToken(user);

        return new UserLoginResponseDto(jwtToken);
    }
}