package com.azx23034.todo.dto;

import com.azx23034.todo.model.User;
import com.azx23034.todo.model.UserRole;

// Respuesta al registrarse o consultar un usuario; excluye el hash de la contraseña que nunca debe salir de la capa de servicio
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
