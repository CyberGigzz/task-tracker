package com.example.tasktracker.service.impl;

import com.example.tasktracker.dto.task.*;
import com.example.tasktracker.exception.EntityNotFoundException;
import com.example.tasktracker.mapper.TaskMapper;
import com.example.tasktracker.model.*;
import com.example.tasktracker.repository.ProjectRepository;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.TaskSpecification;
import com.example.tasktracker.repository.UserRepository;
import com.example.tasktracker.service.TaskService;
import com.example.tasktracker.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public TaskResponseDto createTask(CreateTaskRequestDto requestDto) {
        User currentUser = securityUtils.getCurrentUser();
        Project project = findProjectById(requestDto.getProjectId());

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the project owner can create tasks.");
        }

        Task task = taskMapper.toModel(requestDto);
        task.setProject(project);

        if (requestDto.getAssignedUserId() != null) {
            User assignedUser = findUserById(requestDto.getAssignedUserId());
            task.setAssignedUser(assignedUser);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public Page<TaskResponseDto> getAllTasksByProjectId(Long projectId, TaskStatus status, TaskPriority priority, Pageable pageable) {
        Specification<Task> spec = TaskSpecification.build(projectId, status, priority);
        return taskRepository.findAll(spec, pageable).map(taskMapper::toDto);
    }

    @Override
    public TaskResponseDto getTaskById(Long taskId) {
        Task task = findTaskById(taskId);
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long taskId, UpdateTaskRequestDto requestDto) {
        User currentUser = securityUtils.getCurrentUser();
        Task task = findTaskById(taskId);

        if (!task.getProject().getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the project owner can update task details.");
        }

        if (requestDto.getTitle() != null) task.setTitle(requestDto.getTitle());
        if (requestDto.getDescription() != null) task.setDescription(requestDto.getDescription());
        if (requestDto.getStatus() != null) task.setStatus(requestDto.getStatus());
        if (requestDto.getPriority() != null) task.setPriority(requestDto.getPriority());
        if (requestDto.getDueDate() != null) task.setDueDate(requestDto.getDueDate());

        if (requestDto.getAssignedUserId() != null) {
            User assignedUser = findUserById(requestDto.getAssignedUserId());
            task.setAssignedUser(assignedUser);
        } else {
            task.setAssignedUser(null); 
        }

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTaskStatus(Long taskId, UpdateTaskStatusRequestDto requestDto) {
        User currentUser = securityUtils.getCurrentUser();
        Task task = findTaskById(taskId);

        if (task.getAssignedUser() == null || !task.getAssignedUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the assigned user can update the task status.");
        }

        task.setStatus(requestDto.getStatus());
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        User currentUser = securityUtils.getCurrentUser();
        Task task = findTaskById(taskId);

        if (!task.getProject().getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the project owner can delete tasks.");
        }
        taskRepository.delete(task);
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
    }
    
    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));
    }
    
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
}