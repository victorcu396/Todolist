package com.azx23034.todo.repository;

import com.azx23034.todo.model.Category;
import com.azx23034.todo.model.Task;
import com.azx23034.todo.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor(User user, Sort sort);

    List<Task> findByCategory(Category category);

    @Modifying
    @Query(value = "DELETE FROM task_tag WHERE task_id IN (SELECT id FROM task WHERE author_id = :userId)", nativeQuery = true)
    void deleteTaskTagsByAuthorId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "DELETE FROM task WHERE author_id = :userId", nativeQuery = true)
    void deleteTasksByAuthorId(@Param("userId") Long userId);
}
