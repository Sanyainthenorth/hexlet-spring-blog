package io.hexlet.spring.controller;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.TagMapper;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.repository.TagRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TagDTO> index() {
        List<Tag> tags = tagRepository.findAll();
        return tagMapper.map(tags);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO show(@PathVariable Long id) {
        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        return tagMapper.map(tag);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDTO create(@Valid @RequestBody TagCreateDTO tagData) {
        Tag tag = tagMapper.map(tagData);
        tag = tagRepository.save(tag);
        return tagMapper.map(tag);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO update(@PathVariable Long id, @Valid @RequestBody TagUpdateDTO tagData) {
        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        tagMapper.update(tagData, tag);
        tag = tagRepository.save(tag);
        return tagMapper.map(tag);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        tagRepository.delete(tag);
    }
}
