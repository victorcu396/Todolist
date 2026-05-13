package com.azx23034.todo.controller;

import com.azx23034.todo.model.User;
import com.azx23034.todo.model.UserRole;
import com.azx23034.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Tag(name = "Admin - Usuarios", description = "Administración de usuarios del sistema")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios registrados en el sistema")
    @GetMapping
    public List<User> listUsers() {
        return userService.findAll();
    }

    @Operation(summary = "Cambiar rol", description = "Cambia el rol de un usuario. Valores válidos: USER, GESTOR")
    @PatchMapping("/{id}/role")
    public User changeRole(
            @Parameter(name = "id", description = "Identificador único del usuario", required = true) @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String roleValue = body.get("role");
        if (roleValue == null || roleValue.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El campo 'role' es obligatorio. Valores válidos: USER, GESTOR");
        }
        UserRole role;
        try {
            role = UserRole.valueOf(roleValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Rol no válido: '" + roleValue + "'. Valores válidos: USER, GESTOR");
        }
        try {
            return userService.changeRole(id, role);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Parameter(name = "id", description = "Identificador único del usuario", required = true) @PathVariable Long id) {
        userService.deleteById(id);
    }
}
