package com.glossaar.backend.word;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WordServiceTest {

    @Mock
    private WordRepository repo;

    private WordService service;

    @BeforeEach
    void setUp() {
        service = new WordService(repo);
    }

    @Test
    void getAll_withEmptySearch_usesFindAllWithPagingAndSorting() {
        Page<WordEntity> page = new PageImpl<>(List.of(new WordEntity("alpha", "desc")));
        when(repo.findAll(any(Pageable.class))).thenReturn(page);

        Page<WordEntity> result = service.getAll("", 0, 10, "word", "asc");

        assertEquals(1, result.getContent().size());
        verify(repo, never()).findByWordContainingIgnoreCaseOrExplanationContainingIgnoreCase(anyString(), anyString(), any(Pageable.class));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findAll(pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertEquals("word: ASC", pageable.getSort().toString());
    }

    @Test
    void getAll_withSearch_usesFilterQuery() {
        Page<WordEntity> page = new PageImpl<>(List.of(new WordEntity("lorem", "ipsum")));
        when(repo.findByWordContainingIgnoreCaseOrExplanationContainingIgnoreCase(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<WordEntity> result = service.getAll("lor", 0, 10, "explanation", "desc");

        assertEquals(1, result.getContent().size());
        verify(repo).findByWordContainingIgnoreCaseOrExplanationContainingIgnoreCase(anyString(), anyString(), any(Pageable.class));
        verify(repo, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAll_invalidPage_throws400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.getAll("", -1, 10, "word", "asc"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void create_trimsWord_andNormalizesBlankExplanationToNull() {
        when(repo.save(any(WordEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WordEntity created = service.create("  hello  ", "   ");

        assertEquals("hello", created.getWord());
        assertNull(created.getExplanation());
    }

    @Test
    void patch_updatesOnlyProvidedFields() {
        WordEntity existing = new WordEntity("old", "old explanation");
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(WordEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WordEntity updated = service.patch(1L, " new ", null);

        assertEquals("new", updated.getWord());
        assertEquals("old explanation", updated.getExplanation());
    }

    @Test
    void delete_missingWord_throws404() {
        when(repo.existsById(99L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.delete(99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
