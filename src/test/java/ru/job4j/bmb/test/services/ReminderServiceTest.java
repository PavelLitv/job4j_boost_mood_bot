package ru.job4j.bmb.test.services;

import org.junit.jupiter.api.Test;
import ru.job4j.bmb.model.DailyAdvice;
import ru.job4j.bmb.repositories.DailyAdviceFakeRepository;
import ru.job4j.bmb.repositories.UserFakeRepository;
import ru.job4j.bmb.services.DailyAdviceService;
import ru.job4j.bmb.services.ReminderService;
import ru.job4j.bmb.telegram.SentContentFake;
import ru.job4j.bmb.telergam.TgUI;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repositories.MoodFakeRepository;
import ru.job4j.bmb.repositories.MoodLogFakeRepository;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderServiceTest {
    @Test
    public void whenMoodGood() {
        // Arrange
        var sentContent = new SentContentFake();
        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));
        var moodLogRepository = new MoodLogFakeRepository();
        var user = new User();
        user.setChatId(100);
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        var yesterday = LocalDate.now()
                .minusDays(10)
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC);
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var tgUI = new TgUI(moodRepository);
        // Act
        new ReminderService(sentContent, moodLogRepository, new UserFakeRepository(),
                new DailyAdviceService(moodLogRepository, new DailyAdviceFakeRepository()), tgUI)
                .remindUsers();
        // Assert
        assertThat(sentContent.getResult().iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Good");
    }

    @Test
    public void whenNotRemind() {
        // Arrange
        var sentContent = new SentContentFake();
        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));
        var moodLogRepository = new MoodLogFakeRepository();
        var user = new User();
        user.setChatId(100);
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        var yesterday = LocalDate.now()
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC);
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var tgUI = new TgUI(moodRepository);
        // Act
        new ReminderService(sentContent, moodLogRepository, new UserFakeRepository(),
                new DailyAdviceService(moodLogRepository, new DailyAdviceFakeRepository()), tgUI)
                .remindUsers();
        // Assert
        assertThat(sentContent.getResult().size()).isEqualTo(0);
    }

    @Test
    public void whenEveryDayThenForUserEveryDayTrue() {
        // Arrange
        var sentContent = new SentContentFake();
        var userRepository = new UserFakeRepository();
        var user = new User(1L, 1000, 100, true);
        userRepository.save(user);
        var userWithoutAdvice = new User(2L, 2000, 200, false);
        userRepository.save(userWithoutAdvice);
        var moodRepository = new MoodFakeRepository();
        var mood = new Mood(1L, "Good", true);
        moodRepository.save(mood);
        var moodLogRepository = new MoodLogFakeRepository();
        var moodLog = new MoodLog(1L, user, mood);
        var yesterday = LocalDate.now()
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC);
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var dailyAdviceFakeRepository = new DailyAdviceFakeRepository();
        dailyAdviceFakeRepository.save(new DailyAdvice(1L, "Каждый день — это новая возможность.", true));
        dailyAdviceFakeRepository.save(new DailyAdvice(2L, "Мыслите позитивно.", false));
        var dailyAdviceService = new DailyAdviceService(moodLogRepository, dailyAdviceFakeRepository);
        var tgUI = new TgUI(moodRepository);
        // Act
        new ReminderService(sentContent, moodLogRepository, userRepository, dailyAdviceService, tgUI)
                .everyDay();
        // Assert
        assertThat(sentContent.getResult().size()).isEqualTo(1);
        assertThat(sentContent.getResult().iterator().next().getText())
                .isEqualTo("Каждый день — это новая возможность.");
        assertThat(sentContent.getResult().iterator().next().getChatId()).isEqualTo(100);
    }
}
