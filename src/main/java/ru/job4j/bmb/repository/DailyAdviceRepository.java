package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.DailyAdvice;

import java.util.List;

@Repository
public interface DailyAdviceRepository extends CrudRepository<DailyAdvice, Long> {
    List<DailyAdvice> findByGood(boolean good);
}
