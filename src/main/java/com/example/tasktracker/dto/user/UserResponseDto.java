package com.example.tasktracker.dto.user;

import com.example.tasktracker.model.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private Role role;
}