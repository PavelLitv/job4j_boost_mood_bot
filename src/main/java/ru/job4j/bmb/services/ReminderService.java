package ru.job4j.bmb.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;
import ru.job4j.bmb.telergam.SentContent;
import ru.job4j.bmb.telergam.TgUI;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class ReminderService {
    private final SentContent sentContent;
    private final MoodLogRepository moodLogRepository;
    private final UserRepository userRepository;
    private final DailyAdviceService dailyAdviceService;
    private final TgUI tgUI;

    public ReminderService(
            SentContent sentContent,
            MoodLogRepository moodLogRepository,
            UserRepository userRepository,
            DailyAdviceService dailyAdviceService,
            TgUI tgUI
    ) {
        this.sentContent = sentContent;
        this.moodLogRepository = moodLogRepository;
        this.userRepository = userRepository;
        this.dailyAdviceService = dailyAdviceService;
        this.tgUI = tgUI;
    }

    @Scheduled(fixedRateString = "${remind.period}")
    public void remindUsers() {
        var startOfDay = LocalDate.now()
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC);
        var endOfDay = LocalDate.now()
                .plusDays(1)
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC) - 1;
        for (var user : moodLogRepository.findUsersWhoDidNotVoteToday(startOfDay, endOfDay)) {
            var content = new Content(user.getChatId());
            content.setText("Как настроение?");
            content.setMarkup(tgUI.buildButtons());
            sentContent.sent(content);
        }
    }

    @Scheduled(cron = "${advice.time}")
    public void everyDay() {
        var users = userRepository.findByDailyAdvice(true);
        users.forEach(user -> sentContent.sent(dailyAdviceService.getRandomAdvice(user).get()));
    }
}
