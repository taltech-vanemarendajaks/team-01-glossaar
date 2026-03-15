package com.glossaar.backend.word;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "words")
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String word;

    @Column(length = 1000)
    private String explanation;

    protected WordEntity() {
        // JPA
    }

    public WordEntity(String word, String explanation) {
        this.word = word;
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
