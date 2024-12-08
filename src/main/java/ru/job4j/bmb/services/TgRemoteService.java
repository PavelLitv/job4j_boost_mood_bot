package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.telergam.BotCommandHandler;

@Service
public class TgRemoteService extends TelegramLongPollingBot {
    private final String botName;
    private final String botToken;
    private final BotCommandHandler botCommandHandler;

    public TgRemoteService(@Value("${telegram.bot.name}") String botName,
                           @Value("${telegram.bot.token}") String botToken,
                           BotCommandHandler botCommandHandler) {
        this.botName = botName;
        this.botToken = botToken;
        this.botCommandHandler = botCommandHandler;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
            botCommandHandler.callBackQuery(update.getCallbackQuery())
                    .ifPresent(content -> message.setText(content.getText()));
            send(message);
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            var content = botCommandHandler.commands(update.getMessage());
            if (content.isPresent()) {
                message.setText(content.get().getText());
                message.setReplyMarkup(content.get().getMarkup());
            } else {
                message.setText("This is an unknown command, try another one");
            }
            send(message);
        }
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
