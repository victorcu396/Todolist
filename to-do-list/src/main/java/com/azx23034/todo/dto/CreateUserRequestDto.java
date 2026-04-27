package com.azx23034.todo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        String username,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
        String password,

        String fullname
) {
}
