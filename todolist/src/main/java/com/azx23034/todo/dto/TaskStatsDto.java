package com.azx23034.todo.dto;

public record TaskStatsDto(
        long total,
        long completed,
        long pending,
        long overdue,
        long starred
) {}
