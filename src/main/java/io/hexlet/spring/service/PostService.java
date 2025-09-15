package io.hexlet.spring.service;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.PostMapper;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        Post post = postMapper.map(postData);

        // Обработка тегов
        if (postData.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(postData.getTagIds());
            post.setTags(tags);
        }

        post = postRepository.save(post);
        return postMapper.map(post);
    }

    public PostDTO updatePost(Long id, PostUpdateDTO postData) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));

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
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));
        postRepository.delete(post);
    }
}