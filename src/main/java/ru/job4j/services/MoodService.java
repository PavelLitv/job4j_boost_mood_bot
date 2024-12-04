package ru.job4j.services;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Service;

@Service
public class MoodService implements BeanNameAware {
    @Override
    public void setBeanName(String name) {
        System.out.println("MoodService name: " + name);
    }
}
