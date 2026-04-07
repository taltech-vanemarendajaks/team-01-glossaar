package com.glossaar.backend.quiz;

import com.glossaar.backend.word.WordEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends Repository<WordEntity, Long> {

    @Query(
            value = """
                    select w.*
                    from words w
                    join user_word_scores uws on uws.word_id = w.id
                    where uws.user_id = :userId
                    and coalesce(trim(w.explanation), '') <> ''
                    order by
                        case when uws.last_quizzed_at is null then 0 else 1 end asc,
                        uws.last_quizzed_at asc,
                        uws.quiz_score asc,
                        random()
                    limit :count
                    """,
            nativeQuery = true
    )
    List<WordEntity> findLowestScoreQuizWordsByUserId(
            @Param("userId") Long userId,
            @Param("count") int count
    );

    @Query(
            value = """
                    select w.*
                    from words w
                    join user_word_scores uws on uws.word_id = w.id
                    where uws.user_id = :userId
                    and w.category_id = :categoryId
                    and coalesce(trim(w.explanation), '') <> ''
                    order by
                        case when uws.last_quizzed_at is null then 0 else 1 end asc,
                        uws.last_quizzed_at asc,
                        uws.quiz_score asc,
                        random()
                    limit :count
                    """,
            nativeQuery = true
    )
    List<WordEntity> findLowestScoreQuizWordsByUserIdAndCategoryId(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("count") int count
    );

    @Query(
            value = """
                    select x.explanation
                    from (
                        select distinct w.explanation
                        from words w
                        where coalesce(trim(w.explanation), '') <> ''
                        and w.id <> :excludeWordId
                        and w.explanation <> :excludeExplanation
                    ) x
                    order by random()
                    limit :count
                    """,
            nativeQuery = true
    )
    List<String> findRandomDistractorExplanations(
            @Param("excludeWordId") Long excludeWordId,
            @Param("excludeExplanation") String excludeExplanation,
            @Param("count") int count
    );
}
