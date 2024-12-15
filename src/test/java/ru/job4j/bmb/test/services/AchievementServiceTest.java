package ru.job4j.bmb.test.services;

import org.junit.jupiter.api.Test;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repositories.AchievementRepositoryFake;
import ru.job4j.bmb.repositories.AwardsRepositoryFake;
import ru.job4j.bmb.repositories.MoodLogFakeRepository;
import ru.job4j.bmb.services.AchievementService;
import ru.job4j.bmb.services.UserEvent;
import ru.job4j.bmb.telegram.SentContentFake;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class AchievementServiceTest {
    @Test
    public void whenTwoPotentialAchievementsThenTakeOne() {
        // Arrange
        var sentContent = new SentContentFake();
        var user = new User(1L, 1000, 100, true);
        var mood = new Mood(1L, "Good", true);
        var moodLogRepository = new MoodLogFakeRepository();
        var moodLog = new MoodLog(1L, user, mood);
        var yesterday = LocalDate.now()
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC);
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var awardsRepository = new AwardsRepositoryFake();
        var awardReceived = new Award(1L, "Ничего не делал", "Ачивка за 0 дней", 0);
        var awardExpected = new Award(2L, "1 раз выбрал ок", "Ачивка за 1 день", 1);
        awardsRepository.save(awardReceived);
        awardsRepository.save(awardExpected);
        var achievementRepository = new AchievementRepositoryFake();
        achievementRepository.save(new Achievement(user, awardReceived));
        var achievementService = new AchievementService(
                moodLogRepository,
                awardsRepository,
                achievementRepository,
                sentContent
        );
        // Act
        achievementService.onApplicationEvent(new UserEvent(this, user));
        // Assert
        assertThat(sentContent.getResult().size()).isEqualTo(1);
    }

    @Test
    public void whenGetAchievement() {
        // Arrange
        var sentContent = new SentContentFake();
        var user = new User(1L, 1000, 100, true);
        var mood = new Mood(1L, "Good", true);
        var moodLogRepository = new MoodLogFakeRepository();
        var moodLog = new MoodLog(1L, user, mood);
        var yesterday = LocalDate.now()
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC);
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var awardsRepository = new AwardsRepositoryFake();
        awardsRepository.save(new Award(1L, "1 раз выбрал ок", "Ачивка за 1 день", 1));
        var achievementService = new AchievementService(
                moodLogRepository,
                awardsRepository,
                new AchievementRepositoryFake(),
                sentContent
        );
        // Act
        achievementService.onApplicationEvent(new UserEvent(this, user));
        // Assert
        assertThat(sentContent.getResult().get(0).getText())
                .isEqualTo("Поздравляю! Вы получили достижение:\n1 раз выбрал ок\n");
    }

    @Test
    public void whenGetAchievementsThenUnavailableAwardNotReceived() {
        // Arrange
        var sentContent = new SentContentFake();
        var user = new User(1L, 1000, 100, true);
        var mood = new Mood(1L, "Good", true);
        var moodLogRepository = new MoodLogFakeRepository();
        var moodLog = new MoodLog(1L, user, mood);
        var yesterday = LocalDate.now()
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC);
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var awardsRepository = new AwardsRepositoryFake();
        var awardExpected = new Award(1L, "1 раз выбрал ок", "Ачивка за 1 день", 1);
        var awardUnavailable = new Award(2L, "2 раза выбрал ок", "Ачивка за 2 дня", 2);
        awardsRepository.save(awardExpected);
        awardsRepository.save(awardUnavailable);
        var achievementService = new AchievementService(
                moodLogRepository,
                awardsRepository,
                new AchievementRepositoryFake(),
                sentContent
        );
        // Act
        achievementService.onApplicationEvent(new UserEvent(this, user));
        // Assert
        assertThat(sentContent.getResult().size()).isEqualTo(1);
    }
}
