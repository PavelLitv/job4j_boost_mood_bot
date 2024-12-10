package ru.job4j.bmb.repositories;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repository.MoodContentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoodContentFakeRepository
        extends CrudRepositoryFake<MoodContent, Long>
        implements MoodContentRepository {

    @Override
    public List<MoodContent> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public Optional<MoodContent> findByMoodId(long moodId) {
        return this.memory.values().stream()
                .filter(moodContent -> moodContent.getMood().getId() == moodId)
                .findFirst();
    }
}
