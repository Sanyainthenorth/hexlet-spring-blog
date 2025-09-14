package io.hexlet.spring.controller;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserPatchDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.UserMapper;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(userMapper::toDTO)
                             .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                             .map(userMapper::toDTO)
                             .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        User user = userMapper.toEntity(userCreateDTO);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userData) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        userMapper.update(userData, user);
        user.setUpdatedAt(LocalDateTime.now()); // Добавляем обновление времени

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO patchUser(@PathVariable Long id, @Valid @RequestBody UserPatchDTO userData) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        userMapper.patch(userData, user);
        user.setUpdatedAt(LocalDateTime.now()); // Добавляем обновление времени

        User patchedUser = userRepository.save(user);
        return userMapper.toDTO(patchedUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}

