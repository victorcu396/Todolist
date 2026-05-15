package com.azx23034.todo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// Igual que el de creación pero todos los campos son opcionales para poder actualizar solo lo que el usuario quiere cambiar
public record UpdateUserRequestDto(

        String username,
        String fullname,

        @Email(message = "El email no tiene un formato válido")
        String email,

        @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
        String newPassword
) {
}
