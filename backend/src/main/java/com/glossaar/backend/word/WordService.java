package com.glossaar.backend.word;

import com.glossaar.backend.category.CategoryEntity;
import com.glossaar.backend.category.CategoryService;
import com.glossaar.backend.user.UserEntity;
import com.glossaar.backend.userword.UserWordScoreEntity;
import com.glossaar.backend.userword.UserWordScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository repo;
    private final CategoryService categoryService;
    private final UserWordScoreRepository userWordScoreRepository;

    public Page<WordEntity> getAll(UserEntity user, String search, int page, int size, String sortBy, String sortDir) {
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
            return repo.findAllByUser(user, pageable);
        }

        return repo.findByUserAndWordContainingIgnoreCaseOrUserAndExplanationContainingIgnoreCase(
            user,
            normalizedSearch,
            user,
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

    public List<WordEntity> getAllByCategoryIds(List<Long> categoryIds, UserEntity user) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        return repo.findAllByCategoryIdsAndUser(categoryIds, user);
    }

    @Transactional
    public WordEntity create(String word, String explanation, String categoryName, UserEntity user) {
        String validWord = requireNonBlank("word", word);
        String validExplanation = normalizeOptional(explanation);
        String validCategoryName = requireNonBlank("category", categoryName);

        CategoryEntity category = categoryService.create(validCategoryName, user);

        WordEntity wordToBeAdded = new WordEntity();
        wordToBeAdded.setWord(validWord);
        wordToBeAdded.setExplanation(validExplanation);
        wordToBeAdded.setCategory(category);
        wordToBeAdded.setUser(user);

        WordEntity savedWord = repo.save(wordToBeAdded);
        userWordScoreRepository.save(new UserWordScoreEntity(user, savedWord, 0));
        return savedWord;
    }

    @Transactional
    public WordEntity patch(Long id, String word, String explanation, String categoryName, UserEntity user) {
        WordEntity entity = repo.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found: " + id));


        if (word != null) {
            entity.setWord(requireNonBlank("word", word));
        }
        if (explanation != null) {
            entity.setExplanation(normalizeOptional(explanation));
        }

        String validCategoryName = requireNonBlank("category", categoryName);
        CategoryEntity category = categoryService.create(validCategoryName, user);
        entity.setCategory(category);

        return repo.save(entity);
    }

    @Transactional
    public void delete(Long id, UserEntity user) {
        WordEntity entity = repo.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found: " + id));

        repo.delete(entity);
    }

    private static String requireNonBlank(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must not be blank");
        }
        return value.trim();
    }

    private static String normalizeOptional(String value) {
        requireNonBlank("explanation", value);
        return value.trim();
    }
}
