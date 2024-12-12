package ru.job4j.bmb.repositories;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.DailyAdvice;
import ru.job4j.bmb.repository.DailyAdviceRepository;

import java.util.List;

public class DailyAdviceFakeRepository extends CrudRepositoryFake<DailyAdvice, Long> implements DailyAdviceRepository {
    @Override
    public List<DailyAdvice> findByGood(boolean good) {
        return List.of();
    }
}
