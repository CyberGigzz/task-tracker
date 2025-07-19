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

@ExtendWith(MockitoExtension.class) // Integrates Mockito with JUnit 5
class ProjectServiceImplTest {

    @Mock // Creates a mock instance of ProjectRepository
    private ProjectRepository projectRepository;

    @Mock // Creates a mock instance of ProjectMapper
    private ProjectMapper projectMapper;

    @InjectMocks // Creates an instance of ProjectServiceImpl and injects the mocks into it
    private ProjectServiceImpl projectService;

    private User manager;
    private CreateProjectRequestDto createProjectRequestDto;
    private Project project;
    private ProjectResponseDto projectResponseDto;

    @BeforeEach // This method runs before each test
    void setUp() {
        // Initialize common test objects
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
        // Arrange: Define the behavior of our mocks
        when(projectMapper.toModel(any(CreateProjectRequestDto.class))).thenReturn(project);
        
        Project savedProject = new Project();
        savedProject.setId(10L); // Simulate the project after it's saved and has an ID
        savedProject.setName(project.getName());
        savedProject.setOwner(project.getOwner());
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectResponseDto);

        // Act: Call the method we are testing
        ProjectResponseDto result = projectService.createProject(createProjectRequestDto, manager);

        // Assert: Check if the result is what we expect
        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        assertEquals(10L, result.getId());
        verify(projectRepository).save(any(Project.class)); // Verify that the save method was called
    }

    @Test
    @DisplayName("Get Project By ID - Not Found")
    void getProjectById_WhenProjectDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange: Define the behavior of our mock
        long nonExistentId = 99L;
        when(projectRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert: Check that the correct exception is thrown
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectService.getProjectById(nonExistentId)
        );

        assertEquals("Project not found with id: " + nonExistentId, exception.getMessage());
        verify(projectRepository).findById(nonExistentId); // Verify that findById was called
    }

    // ... (keep all the existing code: class definition, mocks, setUp method, existing tests) ...

    @Test
    @DisplayName("Get Project By ID - Success")
    void getProjectById_WhenProjectExists_ShouldReturnProjectResponseDto() {
        // Arrange
        long existingId = 10L;
        // Our project object from setUp doesn't have an ID, so let's set one for this test
        project.setId(existingId); 
        when(projectRepository.findById(existingId)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        // Act
        ProjectResponseDto result = projectService.getProjectById(existingId);

        // Assert
        assertNotNull(result);
        assertEquals(projectResponseDto.getId(), result.getId());
        assertEquals(projectResponseDto.getName(), result.getName());
        verify(projectRepository).findById(existingId);
        verify(projectMapper).toDto(project);
    }

    @Test
    @DisplayName("Update Project - Success")
    void updateProject_WhenProjectExists_ShouldUpdateAndReturnDto() {
        // Arrange
        long existingId = 10L;
        project.setId(existingId);

        CreateProjectRequestDto updateRequest = new CreateProjectRequestDto();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Description.");

        // When findById is called, return our existing project
        when(projectRepository.findById(existingId)).thenReturn(Optional.of(project));
        // When save is called, just return the project that was passed in
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Mock the DTO mapping
        ProjectResponseDto updatedDto = new ProjectResponseDto();
        updatedDto.setId(existingId);
        updatedDto.setName("Updated Name");
        when(projectMapper.toDto(any(Project.class))).thenReturn(updatedDto);

        // Act
        ProjectResponseDto result = projectService.updateProject(existingId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getName()); // Check that the name was updated
        verify(projectRepository).findById(existingId);
        verify(projectRepository).save(any(Project.class)); // Verify that save was called
    }

    @Test
    @DisplayName("Delete Project - Success")
    void deleteProject_WhenProjectExists_ShouldCallDelete() {
        // Arrange
        long existingId = 10L;
        project.setId(existingId);
        // We use the helper method findProjectById in the service, so we must mock the findById call
        when(projectRepository.findById(existingId)).thenReturn(Optional.of(project));
        
        // Act
        // The delete method returns void, so we just call it.
        // We use assertDoesNotThrow to ensure no exceptions are thrown.
        assertDoesNotThrow(() -> projectService.deleteProject(existingId));

        // Assert
        // Verify that the delete method on the repository was called with our project object.
        verify(projectRepository).delete(project);
    }
}