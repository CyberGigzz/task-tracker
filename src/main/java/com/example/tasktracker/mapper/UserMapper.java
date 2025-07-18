package com.example.tasktracker.mapper;

import com.example.tasktracker.dto.user.UserRegistrationRequestDto;
import com.example.tasktracker.dto.user.UserResponseDto;
import com.example.tasktracker.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);
}