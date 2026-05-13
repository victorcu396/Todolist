package com.azx23034.todo.error;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("No hay una tarea con ese ID: %d".formatted(id));
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}
