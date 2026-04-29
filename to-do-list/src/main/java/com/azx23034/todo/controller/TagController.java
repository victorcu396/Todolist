package com.azx23034.todo.controller;

import com.azx23034.todo.model.Tag;
import com.azx23034.todo.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<Tag> listTags() {
        return tagService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tag createTag(@RequestBody Map<String, String> body) {
        return tagService.create(body.get("name"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable Long id) {
        tagService.deleteById(id);
    }
}
