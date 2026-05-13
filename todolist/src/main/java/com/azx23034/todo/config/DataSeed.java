package com.azx23034.todo.config;

import com.azx23034.todo.dto.CreateTaskRequest;
import com.azx23034.todo.dto.CreateUserRequestDto;
import com.azx23034.todo.model.Category;
import com.azx23034.todo.model.Priority;
import com.azx23034.todo.model.User;
import com.azx23034.todo.model.UserRole;
import com.azx23034.todo.repository.CategoryRepository;
import com.azx23034.todo.repository.UserRepository;
import com.azx23034.todo.service.TaskService;
import com.azx23034.todo.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeed {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        if (userRepository.count() > 0 && categoryRepository.count() > 0) return;
        if (categoryRepository.count() == 0) insertCategories();
        if (userRepository.count() > 0) return;
        User user = insertUsers();
        insertTasks(user);
    }

    private User insertUsers() {
        User user = userService.register(new CreateUserRequestDto("user", "user@user.com", "1234", "Usuario Normal"));

        User admin = userService.register(new CreateUserRequestDto("admin", "admin@admin.com", "1234", "Administrador"));
        userService.changeRole(admin.getId(), UserRole.ADMIN);

        return user;
    }

    private void insertCategories() {
        categoryRepository.save(Category.builder().title("Main").build());
        categoryRepository.save(Category.builder().title("Trabajo").build());
        categoryRepository.save(Category.builder().title("Personal").build());
    }

    private void insertTasks(User author) {
        taskService.createTask(
                CreateTaskRequest.builder()
                        .title("Primera tarea")
                        .description("Hacer la primera tarea del proyecto")
                        .tags("tag1,tag2,tag3")
                        .categoryId(1L)
                        .priority(Priority.HIGH)
                        .starred(true)
                        .deadline(LocalDate.now().plusDays(7))
                        .build(),
                author);

        taskService.createTask(
                CreateTaskRequest.builder()
                        .title("Segunda tarea")
                        .description("Hacer la segunda tarea del proyecto")
                        .tags("tag1,tag2,tag4")
                        .categoryId(1L)
                        .priority(Priority.MEDIUM)
                        .build(),
                author);

        taskService.createTask(
                CreateTaskRequest.builder()
                        .title("Tarea de trabajo")
                        .description("Tarea relacionada con el trabajo")
                        .tags("trabajo,urgente")
                        .categoryId(2L)
                        .priority(Priority.HIGH)
                        .deadline(LocalDate.now().plusDays(3))
                        .build(),
                author);
    }
}
