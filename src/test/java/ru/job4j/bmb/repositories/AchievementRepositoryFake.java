package ru.job4j.bmb.repositories;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.repository.AchievementRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AchievementRepositoryFake extends CrudRepositoryFake<Achievement, Long> implements AchievementRepository {
    @Override
    public List<Achievement> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public List<Achievement> findAllByUserId(long userId) {
        return memory.values().stream()
                .filter(achievement -> achievement.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
}
