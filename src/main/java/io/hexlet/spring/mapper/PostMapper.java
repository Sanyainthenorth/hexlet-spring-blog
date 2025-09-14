package io.hexlet.spring.mapper;

import io.hexlet.spring.dto.PostPatchDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.model.Post;
import org.mapstruct.ReportingPolicy;

@Mapper(
    uses = { JsonNullableMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    @Mapping(target = "userId", source = "user.id")
    PostDTO toDTO(Post post);

    @Mapping(target = "user", ignore = true) // User устанавливается отдельно в контроллере
    Post toEntity(PostCreateDTO dto);

    void update(PostUpdateDTO dto, @MappingTarget Post post);

    void patch(PostPatchDTO dto, @MappingTarget Post post);
}