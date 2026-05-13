package com.azx23034.todo.dto;

import com.azx23034.todo.model.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreateTaskRequest {

    @NotBlank
    protected String title;
    protected String description;
    protected String tags;
    protected Long categoryId;
    protected LocalDate deadline;
    protected Priority priority;
    protected boolean starred;
}
