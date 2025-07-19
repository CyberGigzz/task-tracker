package com.example.tasktracker.service.impl;

import com.example.tasktracker.dto.task.CreateTaskRequestDto;
import com.example.tasktracker.dto.task.UpdateTaskStatusRequestDto;
import com.example.tasktracker.exception.EntityNotFoundException;
import com.example.tasktracker.mapper.TaskMapper;
import com.example.tasktracker.model.*;
import com.example.tasktracker.repository.ProjectRepository;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import com.example.tasktracker.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private SecurityUtils securityUtils; // We must mock our security utility

    @InjectMocks
    private TaskServiceImpl taskService;

    private User manager;
    private User assignedUser;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        manager = new User();
        manager.setId(1L);
        manager.setRole(Role.MANAGER);

        assignedUser = new User();
        assignedUser.setId(2L);
        assignedUser.setRole(Role.USER);

        project = new Project();
        project.setId(10L);
        project.setOwner(manager); // The manager owns the project

        task = new Task();
        task.setId(100L);
        task.setTitle("Test Task");
        task.setProject(project);
        task.setAssignedUser(assignedUser); // The task is assigned to the USER
    }

    @Test
    @DisplayName("Create Task - Success by Project Owner")
    void createTask_WhenUserIsProjectOwner_ShouldCreateTask() {
        // Arrange
        when(securityUtils.getCurrentUser()).thenReturn(manager); // The manager is logged in
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(taskMapper.toModel(any())).thenReturn(new Task());
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setProjectId(project.getId());

        // Act & Assert
        assertDoesNotThrow(() -> taskService.createTask(requestDto));
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Create Task - Failure by Non-Owner")
    void createTask_WhenUserIsNotProjectOwner_ShouldThrowAccessDenied() {
        // Arrange
        when(securityUtils.getCurrentUser()).thenReturn(assignedUser); // A different user is logged in
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setProjectId(project.getId());

        // Act & Assert
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> taskService.createTask(requestDto)
        );
        assertEquals("Only the project owner can create tasks.", exception.getMessage());
    }

    @Test
    @DisplayName("Update Task Status - Success by Assigned User")
    void updateTaskStatus_WhenUserIsAssigned_ShouldUpdateStatus() {
        // Arrange
        when(securityUtils.getCurrentUser()).thenReturn(assignedUser); // The ASSIGNED user is logged in
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        UpdateTaskStatusRequestDto requestDto = new UpdateTaskStatusRequestDto();
        requestDto.setStatus(TaskStatus.IN_PROGRESS);

        // Act & Assert
        assertDoesNotThrow(() -> taskService.updateTaskStatus(task.getId(), requestDto));
        verify(taskRepository).save(task);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    @DisplayName("Update Task Status - Failure by Different User")
    void updateTaskStatus_WhenUserIsNotAssigned_ShouldThrowAccessDenied() {
        // Arrange
        when(securityUtils.getCurrentUser()).thenReturn(manager); // The MANAGER is logged in, but not assigned
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        UpdateTaskStatusRequestDto requestDto = new UpdateTaskStatusRequestDto();
        requestDto.setStatus(TaskStatus.IN_PROGRESS);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> taskService.updateTaskStatus(task.getId(), requestDto)
        );
        assertEquals("Only the assigned user can update the task status.", exception.getMessage());
    }
    
    @Test
    @DisplayName("Get Task By ID - Not Found")
    void getTaskById_WhenTaskDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        long nonExistentId = 999L;
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            EntityNotFoundException.class,
            () -> taskService.getTaskById(nonExistentId)
        );
    }
}