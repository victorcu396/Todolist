package com.azx23034.todo.controller;

import com.azx23034.todo.dto.BulkTaskIdsDto;
import com.azx23034.todo.dto.CreateTaskRequest;
import com.azx23034.todo.dto.EditTaskRequest;
import com.azx23034.todo.dto.TaskStatsDto;
import com.azx23034.todo.model.Priority;
import com.azx23034.todo.model.Task;
import com.azx23034.todo.model.User;
import com.azx23034.todo.security.OwnerCheck;
import com.azx23034.todo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Tareas", description = "Gestión de tareas del usuario autenticado")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final OwnerCheck ownerCheck;

    @Operation(summary = "Listar tareas", description = "Devuelve todas las tareas del usuario con filtros opcionales")
    @GetMapping
    public List<Task> taskList(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "ID de la categoría para filtrar") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filtrar por estado completado") @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Filtrar por prioridad: LOW, MEDIUM, HIGH") @RequestParam(required = false) Priority priority,
            @Parameter(description = "Filtrar solo tareas destacadas") @RequestParam(required = false) Boolean starred,
            @Parameter(description = "Búsqueda por texto en título o descripción") @RequestParam(required = false) String search) {
        return taskService.findAllByUser(user, categoryId, completed, priority, starred, search);
    }

    @Operation(summary = "Tareas próximas", description = "Devuelve las tareas con deadline en los próximos 7 días")
    @GetMapping("/upcoming")
    public List<Task> upcoming(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.findUpcoming(user);
    }

    @Operation(summary = "Tareas vencidas", description = "Devuelve las tareas cuyo deadline ya ha pasado y no están completadas")
    @GetMapping("/overdue")
    public List<Task> overdue(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.findOverdue(user);
    }

    @Operation(summary = "Estadísticas de tareas", description = "Devuelve contadores de tareas totales, completadas y pendientes")
    @GetMapping("/stats")
    public TaskStatsDto stats(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.getStats(user);
    }

    @Operation(summary = "Crear tarea", description = "Crea una nueva tarea para el usuario autenticado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody CreateTaskRequest req,
                           @Parameter(hidden = true) @AuthenticationPrincipal User author) {
        return taskService.createTask(req, author);
    }

    @Operation(summary = "Obtener tarea por ID", description = "Devuelve una tarea por su ID si pertenece al usuario autenticado")
    @GetMapping("/{id}")
    public Task getTask(
            @Parameter(name = "id", description = "Identificador único de la tarea", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        return task;
    }

    @Operation(summary = "Editar tarea", description = "Actualiza todos los campos de una tarea existente")
    @PutMapping("/{id}")
    public Task editTask(
            @Parameter(name = "id", description = "Identificador único de la tarea", required = true) @PathVariable Long id,
            @Valid @RequestBody EditTaskRequest req,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        req.setId(id);
        return taskService.editTask(req);
    }

    @Operation(summary = "Marcar como completada", description = "Alterna el estado completado de la tarea")
    @PatchMapping("/{id}/toggle")
    public Task toggleTask(
            @Parameter(name = "id", description = "Identificador único de la tarea", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        return taskService.toggleComplete(id);
    }

    @Operation(summary = "Destacar tarea", description = "Alterna el estado destacado (estrella) de la tarea")
    @PatchMapping("/{id}/star")
    public Task starTask(
            @Parameter(name = "id", description = "Identificador único de la tarea", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        return taskService.toggleStarred(id);
    }

    @Operation(summary = "Actualizar orden", description = "Cambia el orden de visualización de la tarea")
    @PatchMapping("/{id}/order")
    public Task updateOrder(
            @Parameter(name = "id", description = "Identificador único de la tarea", required = true) @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Task task = taskService.findById(id);
        checkOwnership(task, user);
        return taskService.updateOrder(id, body.get("order"));
    }

    @Operation(summary = "Completar tareas en lote", description = "Marca como completadas múltiples tareas a la vez")
    @PatchMapping("/bulk/complete")
    public List<Task> bulkComplete(@Valid @RequestBody BulkTaskIdsDto dto,
                                   @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.bulkComplete(dto.ids(), user);
    }

    @Operation(summary = "Eliminar tareas en lote", description = "Elimina múltiples tareas a la vez")
    @DeleteMapping("/bulk")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bulkDelete(@Valid @RequestBody BulkTaskIdsDto dto,
                           @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        taskService.bulkDelete(dto.ids(), user);
    }

    @Operation(summary = "Eliminar tareas completadas", description = "Elimina todas las tareas completadas del usuario autenticado")
    @DeleteMapping("/completed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompleted(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        taskService.deleteCompleted(user);
    }

    @Operation(summary = "Eliminar tarea", description = "Elimina una tarea por su ID si pertenece al usuario autenticado")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @Parameter(name = "id", description = "Identificador único de la tarea", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
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
