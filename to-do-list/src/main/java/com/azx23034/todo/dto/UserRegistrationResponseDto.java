package com.azx23034.todo.dto;

import com.azx23034.todo.model.User;
import com.azx23034.todo.model.UserRole;

public record UserRegistrationResponseDto(Long id, String username, String email, String fullname, UserRole role) {

    public static UserRegistrationResponseDto toDto(User user) {
        return new UserRegistrationResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullname(),
                user.getRole()
        );
    }
}
