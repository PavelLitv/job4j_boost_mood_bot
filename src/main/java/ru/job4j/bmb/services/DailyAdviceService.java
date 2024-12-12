package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.DailyAdviceRepository;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.util.Optional;

@Service
public class DailyAdviceService {
    private final MoodLogRepository moodLogRepository;
    private final DailyAdviceRepository dailyAdviceRepository;

    public DailyAdviceService(
            MoodLogRepository moodLogRepository,
            DailyAdviceRepository dailyAdviceRepository
    ) {
        this.moodLogRepository = moodLogRepository;
        this.dailyAdviceRepository = dailyAdviceRepository;
    }

    public Optional<Content> getRandomAdvice(User user) {
        var content = new Content(user.getChatId());
        var moodLog = moodLogRepository
                .findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElse(null);
        if (moodLog != null) {
            var advices = dailyAdviceRepository.findByGood(moodLog.getMood().isGood());
            int index = (int) (Math.random() * advices.size());
            content.setText(advices.get(index).getText());
            return Optional.of(content);
        }
        content.setText("Сегодня советов нет, не переживай и приходи завтра");
        return Optional.of(content);
    }
}
