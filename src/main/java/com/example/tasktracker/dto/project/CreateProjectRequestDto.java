package com.example.tasktracker.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProjectRequestDto {
    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;
}