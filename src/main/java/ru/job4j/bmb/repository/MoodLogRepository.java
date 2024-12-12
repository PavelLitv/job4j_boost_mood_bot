package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();

    List<MoodLog> findByUserId(Long userId);

    Optional<MoodLog> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    default List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart) {
        return findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == userId
                        && moodLog.getCreatedAt() >= weekStart)
                .toList();
    }

    default List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart) {
        return findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == userId
                        && moodLog.getCreatedAt() >= monthStart)
                .toList();
    }

    default List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay) {
        List<User> listAll = findAll().stream()
                .map(MoodLog::getUser)
                .distinct()
                .collect(Collectors.toList());
        List<User> listToday = findAll().stream()
                .filter(moodLog -> moodLog.getCreatedAt() >= startOfDay)
                .filter(moodLog -> moodLog.getCreatedAt() <= endOfDay)
                .map(MoodLog::getUser)
                .distinct()
                .toList();
        listAll.removeAll(listToday);
        return listAll;
    }
}
