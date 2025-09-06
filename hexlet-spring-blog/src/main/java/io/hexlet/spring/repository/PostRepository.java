package io.hexlet.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.hexlet.spring.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}