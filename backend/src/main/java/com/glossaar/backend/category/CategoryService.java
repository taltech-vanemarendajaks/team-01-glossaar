package com.glossaar.backend.category;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<CategoryEntity> getAll() {
        return repository.findAll();
    }

    public CategoryEntity getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Category not found: " + id
            ));
    }

    @Transactional
    public CategoryEntity create(String name) {
        String normalized = normalizeName("name", name);

        return repository.findByName(normalized)
            .orElseGet(() -> repository.save(new CategoryEntity(normalized)));
    }

    @Transactional
    public void update(Long id, String name) {
        CategoryEntity category = getById(id);

        String newName = name.trim();
        if (newName.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        category.setName(newName);
        repository.save(category);
    }

    private static String normalizeName(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                field + " must not be blank"
            );
        }

        String trimmed = value.trim();

        if (trimmed.equals(trimmed.toUpperCase())) {
            return trimmed; // keep "AI", "NASA"
        }

        String lower = trimmed.toLowerCase();
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }
}
