package ru.job4j.bmb.test.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repositories.MoodContentFakeRepository;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.services.RecommendationEngine;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {RecommendationEngine.class, MoodContentFakeRepository.class})
public class RecommendationEngineTest {
    @Autowired
    private MoodContentRepository moodContentRepository;
    @Autowired
    private RecommendationEngine recommendationEngine;

    @Test
    void choose1ThenGetRecommendation() {
        long chatId = 100L;
        String moodContentText = "Ты лучший!!!";
        var expectedContent = new Content(chatId);
        expectedContent.setText(moodContentText);
        var mood1 = new Mood("Настроение так себе", false);
        mood1.setId(1L);
        var mood2 = new Mood("Настроение отличное", true);
        mood2.setId(2L);
        var moodContent = new MoodContent(mood1, moodContentText);
        moodContent.setId(1L);
        var moodContent1 = new MoodContent(mood2, "Красавчик!!!");
        moodContent1.setId(2L);
        moodContentRepository.save(moodContent);
        moodContentRepository.save(moodContent1);
        var actualContent = recommendationEngine.recommendFor(chatId, 1L);
        assertThat(actualContent).isEqualTo(expectedContent);
    }
}
