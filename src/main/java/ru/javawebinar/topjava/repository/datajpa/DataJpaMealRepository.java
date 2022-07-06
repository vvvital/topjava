package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class DataJpaMealRepository implements MealRepository {

    private final Sort SORT_DATE_TIME = Sort.by(Sort.Direction.DESC, "dateTime");
    private final CrudMealRepository crudRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Transactional
    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            User user = DataAccessUtils.singleResult(crudRepository.getById(userId));
            meal.setUser(user);
        } else {
            Meal mealOld = get(meal.getId(), userId);

            return mealOld != null ? crudRepository.save(meal) : null;
        }
        return meal.getUser().getId() == userId ? crudRepository.save(meal) : null;
    }

    @Transactional
    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.deleteById(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = crudRepository.findById(id).orElse(null);
        return meal != null && meal.getUser().getId() == userId ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAll(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        List<Meal> meals = crudRepository.getBetweenHalfOpen(startDateTime, endDateTime, userId);
        for (Meal m : meals
        ) {
            System.out.println(m.toString());
        }
        return meals;
    }
}
