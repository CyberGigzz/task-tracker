package com.example.tasktracker.service;

import com.example.tasktracker.dto.task.*;
import com.example.tasktracker.model.TaskPriority;
import com.example.tasktracker.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponseDto createTask(CreateTaskRequestDto requestDto);

    Page<TaskResponseDto> getAllTasksByProjectId(Long projectId, TaskStatus status, TaskPriority priority, Pageable pageable);

    TaskResponseDto getTaskById(Long taskId);

    TaskResponseDto updateTask(Long taskId, UpdateTaskRequestDto requestDto);

    TaskResponseDto updateTaskStatus(Long taskId, UpdateTaskStatusRequestDto requestDto);

    void deleteTask(Long taskId);
}