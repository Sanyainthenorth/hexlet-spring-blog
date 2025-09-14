package io.hexlet.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.model.Post;

import java.util.List;

@Mapper(
    uses = { JsonNullableMapper.class, ReferenceMapper.class, TagMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "tags", ignore = true)
    Post map(PostCreateDTO dto);

    @Mapping(source = "user.id", target = "userId")
    PostDTO map(Post post);


    @Mapping(target = "tags", ignore = true)
    void update(PostUpdateDTO dto, @MappingTarget Post post);

    List<PostDTO> map(List<Post> posts);
}