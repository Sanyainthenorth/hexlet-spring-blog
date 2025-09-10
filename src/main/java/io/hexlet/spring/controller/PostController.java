package io.hexlet.spring.controller;

import io.hexlet.spring.exception.ResourceNotFoundException;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.hexlet.spring.model.Post;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public Page<Post> getPublishedPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByPublishedTrue(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post, @RequestParam Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postRepository.findById(id)
                             .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @Valid @RequestBody Post postData) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));

        post.setTitle(postData.getTitle());
        post.setContent(postData.getContent());

        return postRepository.save(post);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post with id " + id + " not found");
        }
        postRepository.deleteById(id);
    }
    @GetMapping("/published")
    public Page<Post> getPublishedPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        return postRepository.findByPublishedTrue(pageable);
    }

}
