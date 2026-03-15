package com.glossaar.backend.word;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository repo;

    public Page<WordEntity> getAll(String search, int page, int size, String sortBy, String sortDir) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be >= 0");
        }
        if (size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be between 1 and 100");
        }

        String normalizedSearch = search == null ? "" : search.trim();
        String normalizedSortBy = normalizeSortBy(sortBy);
        Sort.Direction direction = normalizeSortDir(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, normalizedSortBy));

        if (normalizedSearch.isEmpty()) {
            return repo.findAll(pageable);
        }

        return repo.findByWordContainingIgnoreCaseOrExplanationContainingIgnoreCase(
                normalizedSearch,
                normalizedSearch,
                pageable
        );
    }

    private static String normalizeSortBy(String sortBy) {
        if (sortBy == null) {
            return "word";
        }
        String value = sortBy.trim().toLowerCase();
        return switch (value) {
            case "word", "explanation", "id" -> value;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "sortBy must be one of: word, explanation, id"
            );
        };
    }

    private static Sort.Direction normalizeSortDir(String sortDir) {
        if (sortDir == null || sortDir.trim().isEmpty()) {
            return Sort.Direction.ASC;
        }
        String value = sortDir.trim().toLowerCase();
        return switch (value) {
            case "asc" -> Sort.Direction.ASC;
            case "desc", "dsc" -> Sort.Direction.DESC;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sortDir must be asc or desc");
        };
    }

    public WordEntity getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found: " + id));
    }

    public WordEntity create(String word, String explanation) {
        return repo.save(new WordEntity(
                requireNonBlank("word", word),
                normalizeOptional(explanation)
        ));
    }

    public WordEntity patch(Long id, String word, String explanation) {
        WordEntity entity = getById(id);

        if (word != null) {
            entity.setWord(requireNonBlank("word", word));
        }
        if (explanation != null) {
            entity.setExplanation(normalizeOptional(explanation));
        }

        return repo.save(entity);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found: " + id);
        }
        repo.deleteById(id);
    }

    private static String requireNonBlank(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must not be blank");
        }
        return value.trim();
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
