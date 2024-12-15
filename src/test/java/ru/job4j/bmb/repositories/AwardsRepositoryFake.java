package ru.job4j.bmb.repositories;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.repository.AwardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AwardsRepositoryFake extends CrudRepositoryFake<Award, Long> implements AwardRepository {
    @Override
    public List<Award> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public List<Award> findAllByDaysLessThanEqual(int daysBefore) {
        return memory.values().stream()
                .filter(award -> award.getDays() <= daysBefore)
                .collect(Collectors.toList());
    }
}
