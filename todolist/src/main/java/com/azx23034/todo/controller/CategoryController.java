package com.azx23034.todo.controller;

import com.azx23034.todo.model.Category;
import com.azx23034.todo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Categorías", description = "Consulta de categorías disponibles")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Listar categorías", description = "Devuelve todas las categorías disponibles en el sistema")
    @GetMapping
    public List<Category> listCategories() {
        return categoryService.findAll();
    }
}
