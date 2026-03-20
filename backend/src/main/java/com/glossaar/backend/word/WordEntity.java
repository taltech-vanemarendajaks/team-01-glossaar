package com.glossaar.backend.word;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "words")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String word;

    @Column(length = 1000)
    private String explanation;

    public WordEntity(String word, String explanation) {
        this.word = word;
        this.explanation = explanation;
    }
}
