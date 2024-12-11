package ru.job4j.bmb.telergam;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.aop.SentContentException;
import ru.job4j.bmb.config.ProdTelegramBotCondition;
import ru.job4j.bmb.content.Content;

@Service
@Conditional(ProdTelegramBotCondition.class)
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {
    private final String botName;
    private final BotCommandHandler handler;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler) {
        super(botToken);
        this.botName = botName;
        this.handler = handler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::sent);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::sent);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @SuppressWarnings({"checkstyle:MethodLength"})
    @Override
    public void sent(Content content) {
        if (content.getAudio() != null) {
            SendAudio sendAudio = new SendAudio();
            sendAudio.setChatId(content.getChatId());
            sendAudio.setAudio(content.getAudio());
            if (content.getText() != null) {
                sendAudio.setTitle(content.getText());
            }
            try {
                execute(sendAudio);
            } catch (TelegramApiException e) {
                throw new SentContentException(e.getMessage(), e);
            }
        } else if (content.getVideo() != null) {
            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(content.getChatId());
            sendVideo.setVideo(content.getVideo());
            if (content.getText() != null) {
                sendVideo.setCaption(content.getText());
            }
            try {
                execute(sendVideo);
            } catch (TelegramApiException e) {
                throw new SentContentException(e.getMessage(), e);
            }
        } else if (content.getPhoto() != null) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(content.getChatId());
            sendPhoto.setPhoto(content.getPhoto());
            if (content.getText() != null) {
                sendPhoto.setCaption(content.getText());
            }
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                throw new SentContentException(e.getMessage(), e);
            }
        } else if (content.getMarkup() != null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(content.getChatId());
            sendMessage.setReplyMarkup(content.getMarkup());
            if (content.getText() != null) {
                sendMessage.setText(content.getText());
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new SentContentException(e.getMessage(), e);
            }
        } else if (content.getText() != null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(content.getChatId());
            sendMessage.setText(content.getText());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new SentContentException(e.getMessage(), e);
            }
        }
    }
}
