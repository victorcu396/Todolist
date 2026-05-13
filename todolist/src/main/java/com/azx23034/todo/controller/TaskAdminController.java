package com.azx23034.todo.controller;

import com.azx23034.todo.dto.EditTaskRequest;
import com.azx23034.todo.model.Task;
import com.azx23034.todo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Tareas", description = "Administración de todas las tareas del sistema")
@RestController
@RequestMapping("/admin/tasks")
@RequiredArgsConstructor
public class TaskAdminController {

    private final TaskService taskService;

    @Operation(summary = "Listar todas las tareas", description = "Devuelve todas las tareas de todos los usuarios")
    @GetMapping
    public List<Task> adminTaskList() {
        return taskService.findAllAdmin();
    }

    @Operation(summary = "Ver tarea por ID", description = "Devuelve los datos editables de cualquier tarea del sistema")
    @GetMapping("/{id}")
    public EditTaskRequest adminViewTask(
            @Parameter(name = "id", description = "Identificador único de la tarea", required = true) @PathVariable Long id) {
        return EditTaskRequest.of(taskService.findById(id));
    }

    @Operation(summary = "Editar tarea", description = "Actualiza los campos de cualquier tarea del sistema")
    @PutMapping("/{id}")
    public Task adminEditTask(
            @Parameter(name = "id", description = "Identificador único de la tarea", required = true) @PathVariable Long id,
            @Valid @RequestBody EditTaskRequest req) {
        req.setId(id);
        return taskService.editTask(req);
    }

    @Operation(summary = "Eliminar tarea", description = "Elimina cualquier tarea del sistema por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteTask(
            @Parameter(name = "id", description = "Identificador único de la tarea", required = true) @PathVariable Long id) {
        taskService.deleteById(id);
    }
}
