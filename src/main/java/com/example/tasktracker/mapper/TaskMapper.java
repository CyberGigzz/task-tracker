package com.example.tasktracker.mapper;

import com.example.tasktracker.dto.task.CreateTaskRequestDto;
import com.example.tasktracker.dto.task.TaskResponseDto;
import com.example.tasktracker.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    TaskResponseDto toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Task toModel(CreateTaskRequestDto requestDto);
}