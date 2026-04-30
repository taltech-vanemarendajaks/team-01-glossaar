package com.glossaar.backend.category;

import com.glossaar.backend.ValidationException;
import com.glossaar.backend.user.UserEntity;
import com.glossaar.backend.word.WordRepository;
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
    private final WordRepository wordRepository;

    public List<CategoryEntity> getAll(UserEntity user) {
        return repository.findAllByUserOrdered(user);
    }


    public CategoryEntity getById(Long id, UserEntity user) {
        return repository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Category not found: " + id
            ));
    }


    @Transactional
    public CategoryEntity create(String name, UserEntity user) {
        String normalized = normalizeName("name", name);

        return repository.findByNameAndUser(normalized, user)
            .orElseGet(() -> repository.save(new CategoryEntity(normalized, user)));
    }

    @Transactional
    public void update(Long id, String name, UserEntity user) {
        CategoryEntity category = getById(id, user);

        String newName = name.trim();
        if (newName.isEmpty()) {
            throw new ValidationException("field: blank", "name");
        }

        category.setName(newName);
        repository.save(category);
    }

    private static String normalizeName(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("field: blank", field);
        }

        String trimmed = value.trim();

        if (trimmed.equals(trimmed.toUpperCase())) {
            return trimmed; // keep "AI", "NASA"
        }

        String lower = trimmed.toLowerCase();
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }

    @Transactional
    public void delete(Long id, UserEntity user) {
        CategoryEntity category = repository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Category not found: " + id
            ));

        long wordCount = wordRepository.countByCategory_IdAndUser(id, user);

        if (wordCount > 0) {
            throw new ValidationException("category: hasWords", String.valueOf(wordCount));
        }

        repository.delete(category);
    }
}
