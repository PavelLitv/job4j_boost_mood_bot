package ru.job4j.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    @PostConstruct
    public void init() {
        System.out.println("UserRepository is going through @PostConstruct init");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("UserRepository will be destroyed via @PreDestroy");
    }
}
