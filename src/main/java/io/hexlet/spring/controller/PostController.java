package io.hexlet.spring.controller;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostPatchDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.PostMapper;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostMapper postMapper;

    @GetMapping
    public List<PostDTO> getPublishedPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByPublishedTrue(pageable)
                             .getContent()
                             .stream()
                             .map(postMapper::toDTO)
                             .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PostDTO> createPost(
        @Valid @RequestBody PostCreateDTO postCreateDTO,
        @RequestParam Long userId) {

        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        Post post = postMapper.toEntity(postCreateDTO);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toDTO(savedPost));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {
        return postRepository.findById(id)
                             .map(post -> ResponseEntity.ok(postMapper.toDTO(post)))
                             .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
    }

    @PutMapping("/{id}")
    public PostDTO update(@RequestBody @Valid PostUpdateDTO postData, @PathVariable Long id) {
        var post = postRepository.findById(id)
                                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        postMapper.update(postData, post);
        postRepository.save(post);
        return postMapper.toDTO(post);
    }

    @PatchMapping("/{id}")
    public PostDTO patch(@RequestBody @Valid PostPatchDTO postData, @PathVariable Long id) {
        var post = postRepository.findById(id)
                                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        postMapper.patch(postData, post);
        postRepository.save(post);
        return postMapper.toDTO(post);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post with id " + id + " not found");
        }
        postRepository.deleteById(id);
    }
}
