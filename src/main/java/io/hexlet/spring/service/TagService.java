package io.hexlet.spring.service;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.TagMapper;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.TagRepository;
import io.hexlet.spring.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private UserUtils userUtils;

    public List<TagDTO> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tagMapper.map(tags);
    }

    public TagDTO getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        return tagMapper.map(tag);
    }

    public TagDTO createTag(TagCreateDTO tagData) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Tag tag = tagMapper.map(tagData);
        tag = tagRepository.save(tag);
        return tagMapper.map(tag);
    }

    public TagDTO updateTag(Long id, TagUpdateDTO tagData) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        tagMapper.update(tagData, tag);
        tag = tagRepository.save(tag);
        return tagMapper.map(tag);
    }

    public void deleteTag(Long id) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        tagRepository.delete(tag);
    }
}
