package com.example.tasktracker.controller;

import com.example.tasktracker.dto.project.CreateProjectRequestDto;
import com.example.tasktracker.dto.project.ProjectResponseDto;
import com.example.tasktracker.model.User;
import com.example.tasktracker.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Project Management", description = "Endpoints for managing projects")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Tells Swagger that all endpoints here require auth
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create a new project", description = "Creates a new project owned by the current manager. Requires MANAGER role.")
    public ResponseEntity<ProjectResponseDto> createProject(
            @RequestBody @Valid CreateProjectRequestDto requestDto,
            @AuthenticationPrincipal User user) {
        ProjectResponseDto createdProject = projectService.createProject(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get all projects", description = "Retrieves a list of all projects. Requires MANAGER or ADMIN role.")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        List<ProjectResponseDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get a project by ID", description = "Retrieves a single project by its ID. Requires MANAGER or ADMIN role.")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable Long id) {
        ProjectResponseDto project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update a project", description = "Updates an existing project. Requires MANAGER role.")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Long id,
            @RequestBody @Valid CreateProjectRequestDto requestDto) {
        ProjectResponseDto updatedProject = projectService.updateProject(id, requestDto);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete a project", description = "Deletes a project by its ID. Requires MANAGER role.")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}