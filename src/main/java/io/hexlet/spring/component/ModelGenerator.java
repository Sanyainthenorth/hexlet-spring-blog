package io.hexlet.spring.component;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import net.datafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import io.hexlet.spring.model.User;

@Component
public class ModelGenerator {

    private final Faker faker;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder; // Добавьте PasswordEncoder

    public ModelGenerator(Faker faker, UserRepository userRepository,
                          PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.faker = faker;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void generateData() {
        for (int i = 0; i < 5; i++) {
            var user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            user.setPasswordDigest(passwordEncoder.encode("password")); // Установите пароль
            userRepository.save(user);

            var post = new Post();
            post.setTitle(faker.book().title());
            post.setContent(faker.lorem().paragraph());
            post.setPublished(faker.bool().bool());
            post.setUser(user);
            postRepository.save(post);
        }
    }
}
