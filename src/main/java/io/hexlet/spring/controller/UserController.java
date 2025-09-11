package io.hexlet.spring.controller;

import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.UserMapper;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    // GET /api/users - получить всех пользователей (возвращает DTO)
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(userMapper::toDTO)
                             .collect(Collectors.toList());
    }

    // GET /api/users/{id} - получить пользователя по ID (возвращает DTO)
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                             .map(user -> ResponseEntity.ok(userMapper.toDTO(user)))
                             .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    // POST /api/users - создать пользователя (принимает User, возвращает DTO)
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(userMapper.toDTO(savedUser));
    }

    // PUT /api/users/{id} - обновить пользователя (возвращает DTO)
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody User userData) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        // Обновляем только необходимые поля
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());
        // Пароль обычно обновляется отдельным методом!

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    // DELETE /api/users/{id} - удалить пользователя
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}