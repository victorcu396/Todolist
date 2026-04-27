package com.azx23034.todo.controller;

import com.azx23034.todo.model.User;
import com.azx23034.todo.model.UserRole;
import com.azx23034.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @GetMapping
    public List<User> listUsers() {
        return userService.findAll();
    }

    @PatchMapping("/{id}/role")
    public User changeRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String roleValue = body.get("role");
        if (roleValue == null || roleValue.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El campo 'role' es obligatorio. Valores válidos: USER, ADMIN");
        }
        try {
            UserRole role = UserRole.valueOf(roleValue.toUpperCase());
            return userService.changeRole(id, role);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Rol no válido: '" + roleValue + "'. Valores válidos: USER, ADMIN");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
