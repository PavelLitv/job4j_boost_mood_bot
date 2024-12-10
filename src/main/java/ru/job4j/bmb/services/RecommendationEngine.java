package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.MoodContentRepository;

@Service
public class RecommendationEngine {
    private final MoodContentRepository moodContentRepository;

    @Autowired
    public RecommendationEngine(MoodContentRepository moodContentRepository) {
        this.moodContentRepository = moodContentRepository;
    }

    public Content recommendFor(Long chatId, Long moodId) {
        Content content = new Content(chatId);
        moodContentRepository.findByMoodId(moodId)
                .ifPresent(moodContent -> content.setText(moodContent.getText()));
        return content;
    }
}
