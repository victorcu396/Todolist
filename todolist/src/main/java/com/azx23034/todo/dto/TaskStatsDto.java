package com.azx23034.todo.dto;

// Valores calculados en el servicio; no hay entidad equivalente porque no se persisten en base de datos
public record TaskStatsDto(
        long total,
        long completed,
        long pending,
        long overdue,
        long starred
) {}
