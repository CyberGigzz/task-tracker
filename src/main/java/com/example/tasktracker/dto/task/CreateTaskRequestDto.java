package com.example.tasktracker.dto.task;

import com.example.tasktracker.model.TaskPriority;
import com.example.tasktracker.model.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateTaskRequestDto {
    @NotBlank
    private String title;

    private String description;
    
    @NotNull
    private TaskStatus status;
    
    @NotNull
    private TaskPriority priority;
    
    @FutureOrPresent
    private LocalDate dueDate;
    
    @NotNull
    private Long projectId;
    
    private Long assignedUserId;
}