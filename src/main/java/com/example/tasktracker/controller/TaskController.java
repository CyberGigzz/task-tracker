package com.example.tasktracker.controller;

import com.example.tasktracker.dto.task.*;
import com.example.tasktracker.model.TaskPriority;
import com.example.tasktracker.model.TaskStatus;
import com.example.tasktracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Task Management", description = "Endpoints for managing tasks within projects")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create a new task", description = "Creates a new task within a project. Requires MANAGER role.")
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody @Valid CreateTaskRequestDto requestDto) {
        TaskResponseDto createdTask = taskService.createTask(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get tasks by project", description = "Retrieves a paginated and filtered list of tasks for a specific project. Requires authentication.")
    public ResponseEntity<Page<TaskResponseDto>> getTasksByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable) {
        Page<TaskResponseDto> tasks = taskService.getAllTasksByProjectId(projectId, status, priority, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a single task by ID", description = "Retrieves a single task by its ID. Requires authentication.")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long taskId) {
        TaskResponseDto task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update a task's details", description = "Updates the details of a task (title, description, etc.). Requires MANAGER role.")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long taskId,
            @RequestBody @Valid UpdateTaskRequestDto requestDto) {
        TaskResponseDto updatedTask = taskService.updateTask(taskId, requestDto);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{taskId}/status")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update a task's status", description = "Allows an assigned user to update the status of their task. Requires USER role.")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody @Valid UpdateTaskStatusRequestDto requestDto) {
        TaskResponseDto updatedTask = taskService.updateTaskStatus(taskId, requestDto);
        return ResponseEntity.ok(updatedTask);
    }



    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete a task", description = "Deletes a task by its ID. Requires MANAGER role.")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}