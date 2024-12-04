package ru.job4j.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Repository;

@Repository
public class AchievementRepository {
    @PostConstruct
    public void init() {
        System.out.println("AchievementRepository is going through @PostConstruct init");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("AchievementRepository will be destroyed via @PreDestroy");
    }
}
