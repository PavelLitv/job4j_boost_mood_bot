package ru.job4j.bmb.telergam;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.job4j.bmb.config.DevTelegramBotCondition;
import ru.job4j.bmb.content.Content;

@Service
@Conditional(DevTelegramBotCondition.class)
public class TelegramBotServiceFake extends TelegramLongPollingBot implements SentContent {
    private final String botName;

    public TelegramBotServiceFake(@Value("${telegram.bot.name}") String botName,
                                  @Value("${telegram.bot.token}") String botToken) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            System.out.println("Входящее сообщение: " + update.getCallbackQuery().getData());
            var content = new Content(1L);
            content.setText("Ответ на callback");
            sent(content);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            System.out.println("Получена команда: " + update.getMessage().getText());
            var content = new Content(1L);
            content.setText("Ответ на поступившую команду");
            sent(content);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        if (content.getAudio() != null) {
            System.out.println("Отправлен audio контент: ");
            if (content.getText() != null) {
                System.out.println("Отправлена подпись к audio контенту: " + content.getText());
            }
        } else if (content.getVideo() != null) {
            System.out.println("Отправлен video контент: ");
            if (content.getText() != null) {
                System.out.println("Отправлена подпись к video контенту: " + content.getText());
            }
        } else if (content.getPhoto() != null) {
            System.out.println("Отправлен photo контент: ");
            if (content.getText() != null) {
                System.out.println("Отправлена подпись к photo контенту: " + content.getText());
            }
        } else if (content.getMarkup() != null) {
            System.out.println("Отправлен markup контент: ");
            if (content.getText() != null) {
                System.out.println("Отправлена подпись к markup контенту: " + content.getText());
            }
        } else if (content.getText() != null) {
            System.out.println("Отправлено текстовое сообщение: " + content.getText());
        }
    }
}
