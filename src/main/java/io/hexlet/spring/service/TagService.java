package io.hexlet.spring.service;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.TagMapper;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

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
        Tag tag = tagMapper.map(tagData);
        tag = tagRepository.save(tag);
        return tagMapper.map(tag);
    }

    public TagDTO updateTag(Long id, TagUpdateDTO tagData) {
        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        tagMapper.update(tagData, tag);
        tag = tagRepository.save(tag);
        return tagMapper.map(tag);
    }

    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        tagRepository.delete(tag);
    }
}