package ru.job4j.bmb.repositories;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.DailyAdvice;
import ru.job4j.bmb.repository.DailyAdviceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DailyAdviceFakeRepository extends CrudRepositoryFake<DailyAdvice, Long> implements DailyAdviceRepository {
    @Override
    public List<DailyAdvice> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public List<DailyAdvice> findByGood(boolean good) {
        return memory.values().stream()
                .filter(dailyAdvice -> dailyAdvice.isGood() == good)
                .collect(Collectors.toList());
    }
}
