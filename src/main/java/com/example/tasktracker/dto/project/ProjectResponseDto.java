package com.example.tasktracker.dto.project;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private LocalDateTime createDate;
}
