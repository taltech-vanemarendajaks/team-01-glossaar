package com.glossaar.backend.word;

import com.glossaar.backend.category.CategoryEntity;
import com.glossaar.backend.user.UserEntity;
import jakarta.persistence.*;
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

    // TODO: convert to many to many in the future?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public WordEntity(String word, String explanation, UserEntity user) {
        this.word = word;
        this.explanation = explanation;
        this.user = user;
    }
}
