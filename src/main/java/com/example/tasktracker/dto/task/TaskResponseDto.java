package com.example.tasktracker.dto.task;

import com.example.tasktracker.model.TaskPriority;
import com.example.tasktracker.model.TaskStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long projectId;
    private Long assignedUserId;
    private LocalDateTime createDate;
}