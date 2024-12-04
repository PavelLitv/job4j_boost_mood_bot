package ru.job4j.telergam;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class BotCommandHandler {
    @PostConstruct
    public void init() {
        System.out.println("BotCommandHandler is going through @PostConstruct init");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("BotCommandHandler will be destroyed via @PreDestroy");
    }
}
