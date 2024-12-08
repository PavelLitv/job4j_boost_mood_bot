package ru.job4j.bmb.test.services;

import org.junit.jupiter.api.Test;
import ru.job4j.bmb.services.ReminderService;
import ru.job4j.bmb.telergam.SentContent;
import ru.job4j.bmb.telergam.TgUI;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repositories.MoodFakeRepository;
import ru.job4j.bmb.repositories.MoodLogFakeRepository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderServiceTest {
    @Test
    public void whenMoodGood() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };
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
        new ReminderService(sentContent, moodLogRepository, tgUI)
                .remindUsers();
        assertThat(result.iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Good");
    }

    @Test
    public void whenNotRemind() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };
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
        new ReminderService(sentContent, moodLogRepository, tgUI)
                .remindUsers();
        assertThat(result.size()).isEqualTo(0);
    }
}
