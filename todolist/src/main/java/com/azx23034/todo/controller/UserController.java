package com.azx23034.todo.controller;

import com.azx23034.todo.dto.ChangePasswordRequestDto;
import com.azx23034.todo.dto.CreateUserRequestDto;
import com.azx23034.todo.dto.UpdateUserRequestDto;
import com.azx23034.todo.dto.UserRegistrationResponseDto;
import com.azx23034.todo.model.User;
import com.azx23034.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Registro, perfil y cambio de contraseña del usuario")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario en el sistema")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponseDto register(@Valid @RequestBody CreateUserRequestDto dto) {
        return UserRegistrationResponseDto.toDto(userService.register(dto));
    }

    @Operation(summary = "Perfil del usuario", description = "Devuelve los datos del usuario autenticado")
    @GetMapping("/me")
    public UserRegistrationResponseDto getCurrentUser(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return UserRegistrationResponseDto.toDto(user);
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza el nombre o el email del usuario autenticado")
    @PatchMapping("/me")
    public UserRegistrationResponseDto updateProfile(@Valid @RequestBody UpdateUserRequestDto dto,
                                                     @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return UserRegistrationResponseDto.toDto(userService.updateProfile(user, dto));
    }

    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña del usuario autenticado verificando la actual")
    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequestDto dto,
                               @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        userService.changePassword(user, dto);
    }
}
