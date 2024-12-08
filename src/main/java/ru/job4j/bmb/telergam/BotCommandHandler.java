package ru.job4j.bmb.telergam;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;
import ru.job4j.bmb.services.MoodService;

import java.util.Optional;

@Component
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final TgUI tgUI;

    public BotCommandHandler(
            UserRepository userRepository,
            MoodService moodService,
            TgUI tgUI
    ) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
    }

    public Optional<Content> commands(Message message) {
        String command = message.getText();
        Long chatId = message.getChatId();
        Long clientId = message.getFrom().getId();
        switch (command) {
            case "/start" -> {
                return handleStartCommand(chatId, clientId);
            }
            case "/week_mood_log" -> {
                return handleWeekModLog(chatId, clientId);
            }
            case "/month_mood_log" -> {
                return handleMothModLog(chatId, clientId);
            }
            case "/award" -> {
                return handleAward(chatId, clientId);
            }
            default -> {
                return Optional.empty();
            }
        }
    }

    public Optional<Content> handleCallback(CallbackQuery callback) {
        var moodId = Long.valueOf(callback.getData());
        var user = userRepository.findByClientId(callback.getFrom().getId());
        return user.map(value -> moodService.chooseMood(value, moodId));
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        var user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);
        if (userRepository.findByClientId(user.getClientId()).isEmpty()) {
            userRepository.save(user);
        } else {
            System.out.println("This user already exists");
        }
        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }

    private Optional<Content> handleWeekModLog(long chatId, Long clientId) {
        return moodService.weekMoodLogCommand(chatId, clientId);
    }

    private Optional<Content> handleMothModLog(long chatId, Long clientId) {
        return moodService.monthMoodLogCommand(chatId, clientId);
    }

    private Optional<Content> handleAward(long chatId, Long clientId) {
        return moodService.awards(chatId, clientId);
    }
}
