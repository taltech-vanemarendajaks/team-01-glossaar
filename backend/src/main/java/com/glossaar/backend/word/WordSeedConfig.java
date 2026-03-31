package com.glossaar.backend.word;

import com.glossaar.backend.category.CategoryEntity;
import com.glossaar.backend.category.CategoryRepository;
import com.glossaar.backend.user.UserEntity;
import com.glossaar.backend.user.UserRepository;
import com.glossaar.backend.userword.UserWordScoreEntity;
import com.glossaar.backend.userword.UserWordScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Configuration
public class WordSeedConfig {

    private static final Logger log = LoggerFactory.getLogger(WordSeedConfig.class);
    private static final String TEST_USERNAME = "TestUser";
    private static final String TEST_EMAIL = "testuser@local.glossaar";
    private static final String CATEGORY_A = "categoryA";
    private static final String CATEGORY_B = "categoryB";
    private static final String[] LOREM_WORDS = {
            "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
            "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
            "magna", "aliqua", "enim", "minim", "veniam", "quis", "nostrud", "exercitation",
            "ullamco", "laboris", "nisi", "aliquip", "commodo", "consequat", "duis", "aute",
            "irure", "in", "reprehenderit", "voluptate", "velit", "esse", "cillum", "eu",
            "fugiat", "nulla", "pariatur", "excepteur", "sint", "occaecat", "cupidatat",
            "non", "proident", "sunt", "culpa", "qui", "officia", "deserunt", "mollit",
            "anim", "id", "est", "laborum"
    };

    @Bean
    @ConditionalOnProperty(name = "app.words.seed.enabled", havingValue = "true", matchIfMissing = true)
    ApplicationRunner seedWordsIfEmpty(
            WordRepository wordRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            UserWordScoreRepository userWordScoreRepository
    ) {
        return args -> {
            if (wordRepository.count() == 0) {
                CategoryEntity categoryA = categoryRepository.findByName(CATEGORY_A)
                        .orElseGet(() -> categoryRepository.save(new CategoryEntity(CATEGORY_A)));
                CategoryEntity categoryB = categoryRepository.findByName(CATEGORY_B)
                        .orElseGet(() -> categoryRepository.save(new CategoryEntity(CATEGORY_B)));

                List<WordEntity> seedWords = IntStream.rangeClosed(1, 100)
                        .mapToObj(i -> {
                            boolean inCategoryA = i <= 50;
                            WordEntity word = new WordEntity(
                                    (inCategoryA ? "a_" : "b_") + loremWord(i),
                                    loremExplanation(i)
                            );
                            word.setCategory(inCategoryA ? categoryA : categoryB);
                            return word;
                        })
                        .toList();

                wordRepository.saveAll(seedWords);
                log.info("Seeded {} words because table was empty", seedWords.size());
            }

            UserEntity testUser = userRepository.findByUsernameIgnoreCase(TEST_USERNAME)
                    .orElseGet(() -> userRepository.save(new UserEntity(TEST_USERNAME, TEST_EMAIL)));

            List<WordEntity> allWords = wordRepository.findAll();
            Set<Long> existingWordIds = userWordScoreRepository.findAllByUserId(testUser.getId())
                    .stream()
                    .map(score -> score.getWord().getId())
                    .collect(java.util.stream.Collectors.toSet());

            List<UserWordScoreEntity> missingScores = new ArrayList<>();
            for (WordEntity word : allWords) {
                if (!existingWordIds.contains(word.getId())) {
                    missingScores.add(new UserWordScoreEntity(testUser, word, 0));
                }
            }

            if (!missingScores.isEmpty()) {
                userWordScoreRepository.saveAll(missingScores);
            }
            log.info(
                    "Ensured {} has score rows for {} words ({} inserted)",
                    TEST_USERNAME,
                    allWords.size(),
                    missingScores.size()
            );
        };
    }

    private static String loremWord(int index) {
        String base = LOREM_WORDS[(index - 1) % LOREM_WORDS.length];
        int cycle = (index - 1) / LOREM_WORDS.length + 1;
        return cycle == 1 ? base : base + "-" + cycle;
    }

    private static String loremExplanation(int index) {
        int start = (index - 1) % LOREM_WORDS.length;
        StringBuilder sb = new StringBuilder("Lorem ipsum ");
        for (int i = 0; i < 10; i++) {
            if (i > 0) sb.append(' ');
            sb.append(LOREM_WORDS[(start + i) % LOREM_WORDS.length]);
        }
        sb.append('.');
        return sb.toString();
    }
}
