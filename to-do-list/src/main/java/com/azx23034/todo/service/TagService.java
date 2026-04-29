package com.azx23034.todo.service;

import com.azx23034.todo.model.Tag;
import com.azx23034.todo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Tag create(String name) {
        if (name == null || name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del tag es obligatorio");
        return tagRepository.findByName(name.trim())
                .orElseGet(() -> tagRepository.save(Tag.builder().name(name.trim()).build()));
    }

    public void deleteById(Long id) {
        if (!tagRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag no encontrado con id: " + id);
        tagRepository.deleteById(id);
    }
}
