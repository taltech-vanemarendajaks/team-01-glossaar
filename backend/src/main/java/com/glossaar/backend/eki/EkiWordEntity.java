package com.glossaar.backend.eki;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "eki_words",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_eki_words_word_lang_group_explanation",
                columnNames = {"word_normalized", "lang", "homonym_nr", "explanation"}
        )
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EkiWordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String word;

    @Column(name = "word_normalized", nullable = false, length = 255)
    private String wordNormalized;

    @Column(nullable = false, length = 16)
    private String lang;

    @Column(name = "homonym_nr", nullable = false)
    private int homonymNr;

    @Column(nullable = false, length = 2000)
    private String explanation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public EkiWordEntity(String word, String wordNormalized, String lang, int homonymNr, String explanation) {
        this.word = word;
        this.wordNormalized = wordNormalized;
        this.lang = lang;
        this.homonymNr = homonymNr;
        this.explanation = explanation;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
