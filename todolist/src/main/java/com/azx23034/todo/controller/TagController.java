package com.azx23034.todo.controller;

import com.azx23034.todo.model.Tag;
import com.azx23034.todo.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Etiquetas", description = "Gestión de etiquetas para clasificar tareas")
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Listar etiquetas", description = "Devuelve todas las etiquetas registradas en el sistema")
    @GetMapping
    public List<Tag> listTags() {
        return tagService.findAll();
    }

    @Operation(summary = "Crear etiqueta", description = "Crea una nueva etiqueta con el nombre indicado en el cuerpo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tag createTag(@RequestBody Map<String, String> body) {
        return tagService.create(body.get("name"));
    }

    @Operation(summary = "Eliminar etiqueta", description = "Elimina una etiqueta del sistema por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(
            @Parameter(name = "id", description = "Identificador único de la etiqueta", required = true) @PathVariable Long id) {
        tagService.deleteById(id);
    }
}
