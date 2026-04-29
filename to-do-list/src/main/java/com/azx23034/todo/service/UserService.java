package com.azx23034.todo.service;

import com.azx23034.todo.dto.ChangePasswordRequestDto;
import com.azx23034.todo.dto.CreateUserRequestDto;
import com.azx23034.todo.dto.UpdateUserRequestDto;
import com.azx23034.todo.model.User;
import com.azx23034.todo.model.UserRole;
import com.azx23034.todo.repository.TaskRepository;
import com.azx23034.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(CreateUserRequestDto dto) {
        return userRepository.save(
                User.builder()
                        .username(dto.username())
                        .email(dto.email())
                        .fullname(dto.fullname())
                        .password(passwordEncoder.encode(dto.password()))
                        .build()
        );
    }

    public User changeRole(Long userId, UserRole role) {
        return userRepository.findById(userId)
                .map(u -> {
                    u.setRole(role);
                    return userRepository.save(u);
                }).orElse(null);
    }

    public User updateProfile(User user, UpdateUserRequestDto dto) {
        if (dto.username() != null && !dto.username().isBlank())
            user.setUsername(dto.username());
        if (dto.fullname() != null && !dto.fullname().isBlank())
            user.setFullname(dto.fullname());
        if (dto.email() != null && !dto.email().isBlank())
            user.setEmail(dto.email());
        if (dto.newPassword() != null && !dto.newPassword().isBlank())
            user.setPassword(passwordEncoder.encode(dto.newPassword()));
        return userRepository.save(user);
    }

    public void changePassword(User user, ChangePasswordRequestDto dto) {
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword()))
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll(Sort.by("username"));
    }

    @Transactional
    public void deleteById(Long id) {
        taskRepository.deleteTaskTagsByAuthorId(id);
        taskRepository.deleteTasksByAuthorId(id);
        userRepository.deleteById(id);
    }
}
