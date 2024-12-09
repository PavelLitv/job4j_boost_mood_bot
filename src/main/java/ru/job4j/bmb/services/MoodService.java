package ru.job4j.bmb.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final ApplicationEventPublisher publisher;
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(ApplicationEventPublisher publisher,
                       MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository) {
        this.publisher = publisher;
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        Mood mood = new Mood();
        mood.setId(moodId);
        MoodLog moodLog = new MoodLog();
        moodLog.setMood(mood);
        moodLog.setUser(user);
        moodLog.setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        moodLogRepository.save(moodLog);
        publisher.publishEvent(new UserEvent(this, user));
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        long weekStart = LocalDateTime.now().minusWeeks(1).toEpochSecond(ZoneOffset.UTC);
        List<MoodLog> moodLogs = moodLogRepository.findMoodLogsForWeek(clientId, weekStart);
        var content = new Content(chatId);
        content.setText(formatMoodLogs(moodLogs, "This week you have chosen the mood"));
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        long monthStart = LocalDateTime.now().minusMonths(1).toEpochSecond(ZoneOffset.UTC);
        List<MoodLog> moodLogs = moodLogRepository.findMoodLogsForMonth(clientId, monthStart);
        var content = new Content(chatId);
        content.setText(formatMoodLogs(moodLogs, "This month you have chosen the mood"));
        return Optional.of(content);
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        List<Achievement> achievements = achievementRepository.findAll().stream()
                .filter(achievement -> achievement.getUser().getClientId() == clientId)
                .toList();
        var content = new Content(chatId);
        content.setText(formatAchievements(achievements));
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    private String formatAchievements(List<Achievement> achievements) {
        if (achievements.isEmpty()) {
            return "My achievements: achievements not found.";
        }
        var sb = new StringBuilder("My achievements:\n");
        achievements.forEach(achievement -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(achievement.getCreateAt()));
            sb.append(formattedDate).append(": ").append(achievement.getAward().getTitle()).append("\n");
        });
        return sb.toString();
    }
}
