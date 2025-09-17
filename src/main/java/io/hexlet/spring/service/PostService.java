package io.hexlet.spring.service;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.PostMapper;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.TagRepository;
import io.hexlet.spring.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserUtils userUtils;

    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return postMapper.map(posts);
    }

    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));
        return postMapper.map(post);
    }

    public PostDTO createPost(PostCreateDTO postData) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Post post = postMapper.map(postData);
        post.setUser(currentUser); // Устанавливаем автора поста

        // Обработка тегов
        if (postData.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(postData.getTagIds());
            post.setTags(tags);
        }

        post = postRepository.save(post);
        return postMapper.map(post);
    }

    public PostDTO updatePost(Long id, PostUpdateDTO postData) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));

        // Проверяем, что пользователь является автором поста
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only edit your own posts");
        }

        postMapper.update(postData, post);

        // Обновление тегов
        if (postData.getTagIds() != null && postData.getTagIds().isPresent()) {
            List<Tag> tags = tagRepository.findAllById(postData.getTagIds().get());
            post.setTags(tags);
        }

        post = postRepository.save(post);
        return postMapper.map(post);
    }

    public void deletePost(Long id) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));

        // Проверяем, что пользователь является автором поста
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }
}
