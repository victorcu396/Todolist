package com.azx23034.todo.controller;

import com.azx23034.todo.dto.ChangePasswordRequestDto;
import com.azx23034.todo.dto.CreateUserRequestDto;
import com.azx23034.todo.dto.UpdateUserRequestDto;
import com.azx23034.todo.dto.UserRegistrationResponseDto;
import com.azx23034.todo.model.User;
import com.azx23034.todo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponseDto register(@Valid @RequestBody CreateUserRequestDto dto) {
        return UserRegistrationResponseDto.toDto(userService.register(dto));
    }

    @GetMapping("/me")
    public UserRegistrationResponseDto getCurrentUser(@AuthenticationPrincipal User user) {
        return UserRegistrationResponseDto.toDto(user);
    }

    @PatchMapping("/me")
    public UserRegistrationResponseDto updateProfile(@Valid @RequestBody UpdateUserRequestDto dto,
                                                     @AuthenticationPrincipal User user) {
        return UserRegistrationResponseDto.toDto(userService.updateProfile(user, dto));
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequestDto dto,
                               @AuthenticationPrincipal User user) {
        userService.changePassword(user, dto);
    }
}
