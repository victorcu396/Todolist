package com.azx23034.todo.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BulkTaskIdsDto(

        @NotEmpty(message = "La lista de IDs no puede estar vacía")
        List<Long> ids
) {}
