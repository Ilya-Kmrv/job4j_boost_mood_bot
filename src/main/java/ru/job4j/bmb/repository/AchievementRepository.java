package ru.job4j.bmb.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.User;

import java.util.List;

@Repository
public interface AchievementRepository extends CrudRepository<Achievement, Long> {

    List<Achievement> findByUserAndAward(User user, Award award);

    @Query("SELECT a FROM Achievement a JOIN a.user u WHERE u.clientId = :clientId")
    List<Achievement> findByClientId(@Param("clientId") Long clientId);
}