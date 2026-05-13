package com.azx23034.todo.security;

import com.azx23034.todo.model.Task;
import com.azx23034.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerCheck {

    private final TaskRepository taskRepository;

    public boolean check(Task task, Long userId) {
        return task.getAuthor().getId().equals(userId);
    }

    public boolean check(Long taskId, Long userId) {
        return taskRepository.findById(taskId)
                .map(task -> check(task, userId))
                .orElse(false);
    }
}
