package io.hexlet.spring.service;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserPatchDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.UserMapper;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import io.hexlet.spring.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        return userRepository.findAll()
                             .stream()
                             .map(userMapper::toDTO)
                             .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                             .map(userMapper::toDTO)
                             .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        User user = userMapper.toEntity(userCreateDTO);

        // Хешируем пароль перед сохранением
        user.setPasswordDigest(passwordEncoder.encode(userCreateDTO.getPassword()));

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserUpdateDTO userData) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        // Проверяем, что пользователь редактирует свой собственный профиль
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only edit your own profile");
        }

        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        userMapper.update(userData, user);
        user.setUpdatedAt(LocalDateTime.now());

        // Если указан новый пароль, хешируем его
        if (userData.getPassword() != null && !userData.getPassword().isEmpty()) {
            user.setPasswordDigest(passwordEncoder.encode(userData.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    public UserDTO patchUser(Long id, UserPatchDTO userData) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        // Проверяем, что пользователь редактирует свой собственный профиль
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only edit your own profile");
        }

        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        userMapper.patch(userData, user);
        user.setUpdatedAt(LocalDateTime.now());

        User patchedUser = userRepository.save(user);
        return userMapper.toDTO(patchedUser);
    }

    public void deleteUser(Long id) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        // Проверяем, что пользователь удаляет свой собственный профиль
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only delete your own profile");
        }

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}
