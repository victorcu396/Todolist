package com.azx23034.todo.controller;

import com.azx23034.todo.model.Tag;
import com.azx23034.todo.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<Tag> listTags() {
        return tagService.findAll();
    }
}
