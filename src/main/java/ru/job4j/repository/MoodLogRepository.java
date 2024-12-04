package ru.job4j.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Repository;

@Repository
public class MoodLogRepository {
    @PostConstruct
    public void init() {
        System.out.println("MoodLogRepository is going through @PostConstruct init");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("MoodLogRepository will be destroyed via @PreDestroy");
    }
}
