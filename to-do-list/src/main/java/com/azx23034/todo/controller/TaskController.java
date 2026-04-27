package com.azx23034.todo.controller;

import com.azx23034.todo.dto.CreateTaskRequest;
import com.azx23034.todo.dto.EditTaskRequest;
import com.azx23034.todo.model.Priority;
import com.azx23034.todo.model.Task;
import com.azx23034.todo.model.User;
import com.azx23034.todo.security.OwnerCheck;
import com.azx23034.todo.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final OwnerCheck ownerCheck;

    @GetMapping
    public List<Task> taskList(@AuthenticationPrincipal User user,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) Boolean completed,
                               @RequestParam(required = false) Priority priority,
                               @RequestParam(required = false) Boolean starred) {
        return taskService.findAllByUser(user, categoryId, completed, priority, starred);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody CreateTaskRequest req,
                           @AuthenticationPrincipal User author) {
        return taskService.createTask(req, author);
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        return task;
    }

    @PutMapping("/{id}")
    public Task editTask(@PathVariable Long id,
                         @Valid @RequestBody EditTaskRequest req,
                         @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        req.setId(id);
        return taskService.editTask(req);
    }

    @PatchMapping("/{id}/toggle")
    public Task toggleTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        return taskService.toggleComplete(id);
    }

    @PatchMapping("/{id}/star")
    public Task starTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        return taskService.toggleStarred(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        taskService.deleteById(id);
    }

    private void checkOwnership(Task task, User user) {
        if (!ownerCheck.check(task, user.getId())) {
            throw new AccessDeniedException("No tienes permiso para acceder a esta tarea");
        }
    }
}
