package com.azx23034.todo.controller;

import com.azx23034.todo.dto.EditTaskRequest;
import com.azx23034.todo.model.Task;
import com.azx23034.todo.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tasks")
@RequiredArgsConstructor
public class TaskAdminController {

    private final TaskService taskService;

    @GetMapping
    public List<Task> adminTaskList() {
        return taskService.findAllAdmin();
    }

    @GetMapping("/{id}")
    public EditTaskRequest adminViewTask(@PathVariable Long id) {
        return EditTaskRequest.of(taskService.findById(id));
    }

    @PutMapping("/{id}")
    public Task adminEditTask(@PathVariable Long id, @Valid @RequestBody EditTaskRequest req) {
        req.setId(id);
        return taskService.editTask(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
    }
}
