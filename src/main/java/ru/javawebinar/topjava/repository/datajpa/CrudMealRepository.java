package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    @Modifying
    @Query("select m from Meal m where m.user.id=:userId order by m.dateTime desc ")
    List<Meal> findAll(@Param("userId") int userId);

    @Modifying
    @Query("delete from Meal m where m.id=:id and m.user.id=:userId")
    int deleteById(@Param("id") int id,@Param("userId") int userId);

    @Modifying
    @Query("select m from Meal m where m.user.id=?3 and m.dateTime>?1 and m.dateTime<?2 order by m.dateTime DESC")
    List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId);

    @Modifying
    @Query("select u from User u where u.id=:userId")
    List<User> getById(@Param("userId") int userId);

}
