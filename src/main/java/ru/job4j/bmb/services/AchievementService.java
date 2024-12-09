package ru.job4j.bmb.services;

import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.telergam.SentContent;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {
    private final MoodLogRepository moodLogRepository;
    private final AwardRepository awardRepository;
    private final AchievementRepository achievementRepository;
    private final SentContent sentContent;

    public AchievementService(
            MoodLogRepository moodLogRepository,
            AwardRepository awardRepository,
            AchievementRepository achievementRepository,
            SentContent sentContent
    ) {
        this.moodLogRepository = moodLogRepository;
        this.awardRepository = awardRepository;
        this.achievementRepository = achievementRepository;
        this.sentContent = sentContent;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        var user = event.getUser();
        List<Achievement> achievementsReceived = new ArrayList<>();
        var moodLogs = moodLogRepository.findByUserId(user.getId()).stream()
                .filter(moodLog -> moodLog.getMood().isGood())
                .sorted(Comparator.comparingLong(MoodLog::getCreatedAt))
                .toList();
        int maxConsecutiveDays = countDaysConsecutiveMood(moodLogs);
        var awardsPotential = awardRepository.findAllByDaysLessThanEqual(maxConsecutiveDays);
        var awardsExist = achievementRepository.findAllByUserId(user.getId()).stream()
                .map(Achievement::getAward)
                .toList();
        awardsPotential.removeAll(awardsExist);
        awardsPotential.forEach(
                award -> {
                    var achievement = new Achievement();
                    achievement.setUser(user);
                    achievement.setAward(award);
                    achievement.setCreateAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                    achievementsReceived.add(achievement);
                });
        if (!achievementsReceived.isEmpty()) {
            achievementRepository.saveAll(achievementsReceived);
            send(user, achievementsReceived);
        }
    }

    private int countDaysConsecutiveMood(List<MoodLog> moodLogs) {
        int count = 1;
        int maxConsecutiveDays = 1;
        for (int i = 0; i < moodLogs.size() - 1; i++) {
            if (LocalDate.ofInstant(Instant.ofEpochSecond(moodLogs.get(i).getCreatedAt()), ZoneOffset.UTC).plusDays(1)
                    .isEqual(LocalDate.ofInstant(Instant.ofEpochSecond(moodLogs.get(i + 1).getCreatedAt()), ZoneOffset.UTC))) {
                count++;
            } else {
                count = 0;
            }
            maxConsecutiveDays = Math.max(maxConsecutiveDays, count);
        }
        return maxConsecutiveDays;
    }

    private void send(User user, List<Achievement> achievements) {
        StringBuilder sb = new StringBuilder("Поздравляю! Вы получили достижение:\n");
        Content content = new Content(user.getChatId());
        achievements.forEach(achievement -> sb.append(achievement.getAward().getTitle()).append("\n"));
        content.setText(sb.toString());
        sentContent.sent(content);
    }
}
