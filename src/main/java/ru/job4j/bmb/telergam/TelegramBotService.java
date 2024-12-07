package ru.job4j.bmb.telergam;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class TelegramBotService {
    @PostConstruct
    public void init() {
        System.out.println("TelegramBotService is going through @PostConstruct init");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("TelegramBotService will be destroyed via @PreDestroy.");
    }
}
