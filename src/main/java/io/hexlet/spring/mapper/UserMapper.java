package io.hexlet.spring.mapper;

import io.hexlet.spring.dto.UserPatchDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.model.User;
import org.mapstruct.ReportingPolicy;

@Mapper(
    uses = { JsonNullableMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserDTO toDTO(User user);

    User toEntity(UserCreateDTO dto);

    @Mapping(target = "passwordDigest", ignore = true)
    void update(UserUpdateDTO dto, @MappingTarget User user);

    void patch(UserPatchDTO dto, @MappingTarget User user);
}
