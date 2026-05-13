package com.azx23034.todo.dto;

import com.azx23034.todo.model.Tag;
import com.azx23034.todo.model.Task;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EditTaskRequest extends CreateTaskRequest {

    private Long id;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;

    public static EditTaskRequest of(Task task) {
        return EditTaskRequest.builder()
                .id(task.getId())
                .completed(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .username(task.getAuthor().getUsername())
                .title(task.getTitle())
                .description(task.getDescription())
                .categoryId(task.getCategory() != null ? task.getCategory().getId() : null)
                .tags(task.getTags().stream().map(Tag::getName).collect(Collectors.joining(", ")))
                .deadline(task.getDeadline())
                .priority(task.getPriority())
                .starred(task.isStarred())
                .build();
    }
}
