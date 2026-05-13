package com.azx23034.todo.service;

import com.azx23034.todo.dto.CreateTaskRequest;
import com.azx23034.todo.dto.EditTaskRequest;
import com.azx23034.todo.dto.TaskStatsDto;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagService tagService;

    public List<Task> findAllByUser(User user, Long categoryId, Boolean completed, Priority priority, Boolean starred, String search) {
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
        if (search != null && !search.isBlank()) {
            String lower = search.toLowerCase();
            result = result.stream()
                    .filter(t -> (t.getTitle() != null && t.getTitle().toLowerCase().contains(lower))
                              || (t.getDescription() != null && t.getDescription().toLowerCase().contains(lower)))
                    .toList();
        }

        return result;
    }

    public List<Task> findUpcoming(User user) {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(7);
        return taskRepository.findByAuthor(user, Sort.by("deadline").ascending()).stream()
                .filter(t -> !t.isCompleted()
                          && t.getDeadline() != null
                          && !t.getDeadline().isBefore(today)
                          && !t.getDeadline().isAfter(limit))
                .toList();
    }

    public List<Task> findOverdue(User user) {
        LocalDate today = LocalDate.now();
        return taskRepository.findByAuthor(user, Sort.by("deadline").ascending()).stream()
                .filter(t -> !t.isCompleted()
                          && t.getDeadline() != null
                          && t.getDeadline().isBefore(today))
                .toList();
    }

    public List<Task> bulkComplete(List<Long> ids, User user) {
        List<Task> tasks = taskRepository.findAllById(ids).stream()
                .filter(t -> t.getAuthor() != null && t.getAuthor().getId().equals(user.getId()))
                .toList();
        tasks.forEach(t -> t.setCompleted(true));
        return taskRepository.saveAll(tasks);
    }

    public void bulkDelete(List<Long> ids, User user) {
        List<Task> tasks = taskRepository.findAllById(ids).stream()
                .filter(t -> t.getAuthor() != null && t.getAuthor().getId().equals(user.getId()))
                .toList();
        taskRepository.deleteAll(tasks);
    }

    public void deleteCompleted(User user) {
        List<Task> completed = taskRepository.findByAuthor(user, Sort.unsorted()).stream()
                .filter(Task::isCompleted)
                .toList();
        taskRepository.deleteAll(completed);
    }

    public TaskStatsDto getStats(User user) {
        List<Task> all = taskRepository.findByAuthor(user, Sort.unsorted());
        LocalDate today = LocalDate.now();
        long completedCount = all.stream().filter(Task::isCompleted).count();
        long overdueCount = all.stream()
                .filter(t -> !t.isCompleted() && t.getDeadline() != null && t.getDeadline().isBefore(today))
                .count();
        long starredCount = all.stream().filter(Task::isStarred).count();
        return new TaskStatsDto(all.size(), completedCount, all.size() - completedCount, overdueCount, starredCount);
    }

    public Task updateOrder(Long id, Integer order) {
        Task task = findById(id);
        task.setTaskOrder(order);
        return taskRepository.save(task);
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

        if (req.getCategoryId() != null && req.getCategoryId() != -1L) {
            Category category = categoryRepository.getReferenceById(req.getCategoryId());
            task.setCategory(category);
        }

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
