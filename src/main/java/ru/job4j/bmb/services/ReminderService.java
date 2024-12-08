package ru.job4j.bmb.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.telergam.SentContent;
import ru.job4j.bmb.telergam.TgUI;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class ReminderService {
    private final SentContent sentContent;
    private final MoodLogRepository moodLogRepository;
    private final TgUI tgUI;

    public ReminderService(SentContent sentContent,
                           MoodLogRepository moodLogRepository, TgUI tgUI) {
        this.sentContent = sentContent;
        this.moodLogRepository = moodLogRepository;
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
}
