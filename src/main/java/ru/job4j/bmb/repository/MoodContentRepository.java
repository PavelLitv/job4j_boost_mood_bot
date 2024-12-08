package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodContent;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoodContentRepository extends CrudRepository<MoodContent, Long> {
    List<MoodContent> findAll();

    Optional<MoodContent> findByMoodId(long moodId);
}
