package com.example.tasktracker.service.impl;

import com.example.tasktracker.dto.project.CreateProjectRequestDto;
import com.example.tasktracker.dto.project.ProjectResponseDto;
import com.example.tasktracker.exception.EntityNotFoundException;
import com.example.tasktracker.mapper.ProjectMapper;
import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.Role;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) 
class ProjectServiceImplTest {

    @Mock 
    private ProjectRepository projectRepository;

    @Mock 
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User manager;
    private CreateProjectRequestDto createProjectRequestDto;
    private Project project;
    private ProjectResponseDto projectResponseDto;

    @BeforeEach 
    void setUp() {
        manager = new User("manager@test.com", "password", Role.MANAGER);
        manager.setId(1L);

        createProjectRequestDto = new CreateProjectRequestDto();
        createProjectRequestDto.setName("Test Project");
        createProjectRequestDto.setDescription("A project for testing.");

        project = new Project();
        project.setName(createProjectRequestDto.getName());
        project.setDescription(createProjectRequestDto.getDescription());
        project.setOwner(manager);

        projectResponseDto = new ProjectResponseDto();
        projectResponseDto.setId(10L);
        projectResponseDto.setName("Test Project");
        projectResponseDto.setOwnerId(1L);
    }

    @Test
    @DisplayName("Create Project - Success")
    void createProject_WhenDataIsValid_ShouldReturnProjectResponseDto() {
        when(projectMapper.toModel(any(CreateProjectRequestDto.class))).thenReturn(project);
        
        Project savedProject = new Project();
        savedProject.setId(10L);
        savedProject.setName(project.getName());
        savedProject.setOwner(project.getOwner());
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.createProject(createProjectRequestDto, manager);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        assertEquals(10L, result.getId());
        verify(projectRepository).save(any(Project.class)); 
    }

    @Test
    @DisplayName("Get Project By ID - Not Found")
    void getProjectById_WhenProjectDoesNotExist_ShouldThrowEntityNotFoundException() {
        long nonExistentId = 99L;
        when(projectRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectService.getProjectById(nonExistentId)
        );

        assertEquals("Project not found with id: " + nonExistentId, exception.getMessage());
        verify(projectRepository).findById(nonExistentId); 
    }


    @Test
    @DisplayName("Get Project By ID - Success")
    void getProjectById_WhenProjectExists_ShouldReturnProjectResponseDto() {
        long existingId = 10L;
        project.setId(existingId); 
        when(projectRepository.findById(existingId)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.getProjectById(existingId);

        assertNotNull(result);
        assertEquals(projectResponseDto.getId(), result.getId());
        assertEquals(projectResponseDto.getName(), result.getName());
        verify(projectRepository).findById(existingId);
        verify(projectMapper).toDto(project);
    }

    @Test
    @DisplayName("Update Project - Success")
    void updateProject_WhenProjectExists_ShouldUpdateAndReturnDto() {
        long existingId = 10L;
        project.setId(existingId);

        CreateProjectRequestDto updateRequest = new CreateProjectRequestDto();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Description.");

        when(projectRepository.findById(existingId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        ProjectResponseDto updatedDto = new ProjectResponseDto();
        updatedDto.setId(existingId);
        updatedDto.setName("Updated Name");
        when(projectMapper.toDto(any(Project.class))).thenReturn(updatedDto);

        ProjectResponseDto result = projectService.updateProject(existingId, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName()); 
        verify(projectRepository).findById(existingId);
        verify(projectRepository).save(any(Project.class)); 
    }

    @Test
    @DisplayName("Delete Project - Success")
    void deleteProject_WhenProjectExists_ShouldCallDelete() {
        long existingId = 10L;
        project.setId(existingId);
        when(projectRepository.findById(existingId)).thenReturn(Optional.of(project));
        
        assertDoesNotThrow(() -> projectService.deleteProject(existingId));

        verify(projectRepository).delete(project);
    }
}