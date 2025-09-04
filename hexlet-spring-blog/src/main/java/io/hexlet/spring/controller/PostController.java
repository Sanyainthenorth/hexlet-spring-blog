package io.hexlet.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.hexlet.spring.model.Post;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private List<Post> posts = new ArrayList<Post>();

    @GetMapping // Список постов 200
    public ResponseEntity<List<Post>> index(@RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok()
                             .body(posts.stream().limit(limit).toList());
    }

    @PostMapping // Создание поста 201
    public ResponseEntity<Post> create(@Valid @RequestBody Post post) {
        posts.add(post);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(post);
    }

    @GetMapping("/{id}") // Получение одного поста 200/404
    public ResponseEntity<Post> show(@PathVariable String id) {
        Optional<Post> post = posts.stream()
                                   .filter(p -> p.getSlug().equals(id))
                                   .findFirst();

        return post.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}") // обновление 200/404
    public ResponseEntity<Post> update(@PathVariable String id, @Valid @RequestBody Post data) {
        Optional<Post> maybePost = posts.stream()
                                        .filter(p -> p.getSlug().equals(id))
                                        .findFirst();

        if (maybePost.isPresent()) {
            Post post = maybePost.get();
            post.setSlug(data.getSlug());
            post.setTitle(data.getTitle());
            post.setContent(data.getContent());
            post.setAuthor(data.getAuthor());
            return ResponseEntity.ok(post);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}") // Удаление страницы 204/404
    public ResponseEntity<Void> destroy(@PathVariable String id) {
        boolean removed = posts.removeIf(p -> p.getSlug().equals(id));

        if (removed) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                                                           errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}
