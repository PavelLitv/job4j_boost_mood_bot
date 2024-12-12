package ru.job4j.bmb.repositories;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserFakeRepository extends CrudRepositoryFake<User, Long> implements UserRepository {
    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Optional<User> findByClientId(Long clientId) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public List<User> findByDailyAdvice(boolean dailyAdvice) {
        return List.of();
    }
}
