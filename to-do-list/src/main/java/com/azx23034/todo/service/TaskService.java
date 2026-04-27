package com.azx23034.todo.service;

import com.azx23034.todo.dto.CreateTaskRequest;
import com.azx23034.todo.dto.EditTaskRequest;
import com.azx23034.todo.error.TaskNotFoundException;
import com.azx23034.todo.model.Category;
import com.azx23034.todo.model.Priority;
import com.azx23034.todo.model.Task;
import com.azx23034.todo.model.User;
import com.azx23034.todo.repository.CategoryRepository;
import com.azx23034.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagService tagService;

    public List<Task> findAllByUser(User user, Long categoryId, Boolean completed, Priority priority, Boolean starred) {
        Sort sort = Sort.by("createdAt").ascending();
        List<Task> result = taskRepository.findByAuthor(user, sort);

        if (categoryId != null)
            result = result.stream().filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId)).toList();
        if (completed != null)
            result = result.stream().filter(t -> t.isCompleted() == completed).toList();
        if (priority != null)
            result = result.stream().filter(t -> t.getPriority() == priority).toList();
        if (starred != null)
            result = result.stream().filter(t -> t.isStarred() == starred).toList();

        return result;
    }

    public List<Task> findAllAdmin() {
        return taskRepository.findAll(Sort.by("createdAt").ascending());
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task createTask(CreateTaskRequest req, User author) {
        return createOrEditTask(req, author);
    }

    public Task editTask(EditTaskRequest req) {
        return createOrEditTask(req, null);
    }

    private Task createOrEditTask(CreateTaskRequest req, User author) {
        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .deadline(req.getDeadline())
                .priority(req.getPriority() != null ? req.getPriority() : Priority.MEDIUM)
                .starred(req.isStarred())
                .build();

        if (req.getCategoryId() == null || req.getCategoryId() == -1L)
            req.setCategoryId(1L);
        Category category = categoryRepository.getReferenceById(req.getCategoryId());
        task.setCategory(category);

        if (req.getTags() != null && !req.getTags().isBlank()) {
            List<String> textTags = Arrays.stream(req.getTags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
            task.getTags().addAll(tagService.saveOrGet(textTags));
        }

        if (req instanceof EditTaskRequest editReq) {
            Task oldTask = findById(editReq.getId());
            task.setId(oldTask.getId());
            task.setCreatedAt(oldTask.getCreatedAt());
            task.setAuthor(oldTask.getAuthor());
            task.setCompleted(editReq.isCompleted());
        } else {
            task.setAuthor(author);
        }

        return taskRepository.save(task);
    }

    public Task toggleComplete(Long id) {
        Task task = findById(id);
        task.setCompleted(!task.isCompleted());
        return taskRepository.save(task);
    }

    public Task toggleStarred(Long id) {
        Task task = findById(id);
        task.setStarred(!task.isStarred());
        return taskRepository.save(task);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> updateCategory(Category oldCategory, Category newCategory) {
        List<Task> tasks = taskRepository.findByCategory(oldCategory);
        tasks.forEach(t -> t.setCategory(newCategory));
        taskRepository.saveAll(tasks);
        return tasks;
    }
}
