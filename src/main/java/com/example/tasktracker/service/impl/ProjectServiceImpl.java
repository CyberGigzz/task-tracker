package com.example.tasktracker.service.impl;

import com.example.tasktracker.dto.project.CreateProjectRequestDto;
import com.example.tasktracker.dto.project.ProjectResponseDto;
import com.example.tasktracker.exception.EntityNotFoundException;
import com.example.tasktracker.mapper.ProjectMapper;
import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.ProjectRepository;
import com.example.tasktracker.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service 
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponseDto createProject(CreateProjectRequestDto requestDto, User owner) {
        Project project = projectMapper.toModel(requestDto);
        project.setOwner(owner); 
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    @Override
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public ProjectResponseDto getProjectById(Long id) {
        Project project = findProjectById(id);
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectResponseDto updateProject(Long id, CreateProjectRequestDto requestDto) {
        Project project = findProjectById(id);

        project.setName(requestDto.getName());
        project.setDescription(requestDto.getDescription());
        
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toDto(updatedProject);
    }

    @Override
    public void deleteProject(Long id) {
        Project project = findProjectById(id);
        projectRepository.delete(project);
    }
    
    private Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
    }
}