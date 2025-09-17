package io.hexlet.spring.controller;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.service.TagService;
import io.hexlet.spring.util.UserUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final UserUtils userUtils;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TagDTO> index() {
        return tagService.getAllTags();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO show(@PathVariable Long id) {
        return tagService.getTagById(id);
    }

    @PostMapping
    public ResponseEntity<TagDTO> create(@Valid @RequestBody TagCreateDTO tagData) {
        try {
            TagDTO createdTag = tagService.createTag(tagData);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> update(@PathVariable Long id, @Valid @RequestBody TagUpdateDTO tagData) {
        try {
            TagDTO updatedTag = tagService.updateTag(id, tagData);
            return ResponseEntity.ok(updatedTag);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            tagService.deleteTag(id);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
