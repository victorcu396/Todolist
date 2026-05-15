package com.azx23034.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Separado del UpdateUserRequestDto porque cambiar la contraseña requiere verificar la actual primero
public record ChangePasswordRequestDto(

        @NotBlank(message = "La contraseña actual es obligatoria")
        String currentPassword,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 4, message = "La nueva contraseña debe tener al menos 4 caracteres")
        String newPassword
) {}
