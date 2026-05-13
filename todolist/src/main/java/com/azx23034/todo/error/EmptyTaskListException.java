package com.azx23034.todo.error;

public class EmptyTaskListException extends RuntimeException {
    public EmptyTaskListException() {
        super("La lista de tareas está vacía");
    }
}
