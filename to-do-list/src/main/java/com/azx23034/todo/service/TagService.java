package com.azx23034.todo.service;

import com.azx23034.todo.model.Tag;
import com.azx23034.todo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> saveOrGet(List<String> tags) {
        List<Tag> result = new ArrayList<>();
        tags.forEach(tag -> {
            Optional<Tag> val = tagRepository.findByName(tag);
            result.add(val.orElseGet(() -> tagRepository.save(Tag.builder().name(tag).build())));
        });
        return result;
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }
}
