package com.azx23034.todo.controller;

import com.azx23034.todo.model.Category;
import com.azx23034.todo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Categorías", description = "Administración de categorías del sistema")
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    @Operation(summary = "Listar categorías", description = "Devuelve todas las categorías del sistema")
    @GetMapping
    public List<Category> listCategories() {
        return categoryService.findAll();
    }

    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría en el sistema")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @Operation(summary = "Actualizar categoría", description = "Actualiza el nombre de una categoría existente")
    @PutMapping("/{id}")
    public Category updateCategory(
            @Parameter(name = "id", description = "Identificador único de la categoría", required = true) @PathVariable Long id,
            @RequestBody Category category) {
        category.setId(id);
        return categoryService.save(category);
    }

    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría del sistema por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @Parameter(name = "id", description = "Identificador único de la categoría", required = true) @PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
