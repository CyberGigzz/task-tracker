package com.example.tasktracker.dto.task;

import com.example.tasktracker.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusRequestDto {
    @NotNull
    private TaskStatus status;
}