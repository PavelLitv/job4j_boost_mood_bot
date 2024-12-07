package ru.job4j.bmb.telergam;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.UserRepository;

@Component
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodContentRepository moodContentRepository;

    public BotCommandHandler(UserRepository userRepository, MoodContentRepository moodContentRepository) {
        this.userRepository = userRepository;
        this.moodContentRepository = moodContentRepository;
    }

    public void start(Message message) {
        long chatId = message.getChatId();
        var user = new User();
        user.setClientId(message.getFrom().getId());
        user.setChatId(chatId);
        if (userRepository.findByClientId(user.getClientId()) == null) {
            userRepository.save(user);
        } else {
            System.out.println("This user already exists");
        }
    }

    public String callBackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        MoodContent moodContent = moodContentRepository.findAll().stream()
                .filter(mood -> mood.getMood().getId() == Long.parseLong(data))
                .findFirst()
                .orElse(new MoodContent("К сожалению рекомендаций нет. Попробуйте позже."));
        return moodContent.getText();
    }
}
