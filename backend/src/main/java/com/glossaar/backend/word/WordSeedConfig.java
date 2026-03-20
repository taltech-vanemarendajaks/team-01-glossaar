package com.glossaar.backend.word;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.IntStream;

@Configuration
public class WordSeedConfig {

    private static final Logger log = LoggerFactory.getLogger(WordSeedConfig.class);
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
    ApplicationRunner seedWordsIfEmpty(WordRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }

            List<WordEntity> seedWords = IntStream.rangeClosed(1, 100)
                    .mapToObj(i -> new WordEntity(
                            loremWord(i),
                            loremExplanation(i)
                    ))
                    .toList();

            repo.saveAll(seedWords);
            log.info("Seeded {} words because table was empty", seedWords.size());
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
