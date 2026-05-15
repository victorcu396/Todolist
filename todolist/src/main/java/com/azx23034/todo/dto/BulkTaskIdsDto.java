package com.azx23034.todo.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

// Agrupa los IDs en una sola petición para no tener que hacer una llamada por cada tarea
public record BulkTaskIdsDto(

        @NotEmpty(message = "La lista de IDs no puede estar vacía")
        List<Long> ids
) {}
