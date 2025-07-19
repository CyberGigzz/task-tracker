package com.example.tasktracker.service;

import com.example.tasktracker.dto.project.CreateProjectRequestDto;
import com.example.tasktracker.dto.project.ProjectResponseDto;
import com.example.tasktracker.model.User;
import java.util.List;

public interface ProjectService {
    ProjectResponseDto createProject(CreateProjectRequestDto requestDto, User owner);

    List<ProjectResponseDto> getAllProjects();
    
    ProjectResponseDto getProjectById(Long id);
    
    ProjectResponseDto updateProject(Long id, CreateProjectRequestDto requestDto);
    
    void deleteProject(Long id);
}