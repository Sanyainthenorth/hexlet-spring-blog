package io.hexlet.spring.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.hexlet.spring.model.Post;
import jakarta.validation.Valid;

@RestController
public class PostController {

    private List<Post> posts = new ArrayList<Post>();

    @GetMapping("/posts") // Список постов
    public List<Post> index(@RequestParam(defaultValue = "10") Integer limit) {
        return posts.stream().limit(limit).toList();
    }

    @PostMapping("/posts") // Создание поста
    public Post create(@Valid @RequestBody Post post) {
        posts.add(post);
        return post;
    }

    @GetMapping("/posts/{id}") // Получение одного поста
    public Optional<Post> show(@PathVariable String id) {
        var post = posts.stream()
                        .filter(p -> p.getSlug().equals(id))
                        .findFirst();
        return post;
    }

    @PutMapping("/posts/{id}")
    public Post update(@PathVariable String id, @Valid @RequestBody Post data) {
        var maybePost = posts.stream()
                             .filter(p -> p.getSlug().equals(id))
                             .findFirst();
        if (maybePost.isPresent()) {
            var post = maybePost.get();
            post.setSlug(data.getSlug());
            post.setTitle(data.getTitle());
            post.setContent(data.getContent());
            post.setAuthor(data.getAuthor());
        }
        return data;
    }

    @DeleteMapping("/posts/{id}") // Удаление страницы
    public void destroy(@PathVariable String id) {
        posts.removeIf(p -> p.getSlug().equals(id));
    }

}
