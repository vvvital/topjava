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


    @Override
    public Meal save(Meal meal, int userId) {
        Meal mealOld = null;
        if (meal.isNew()) {
            User user = DataAccessUtils.singleResult(crudRepository.getById(userId));
            meal.setUser(user);
            return crudRepository.save(meal);
        } else {
            mealOld = get(meal.getId(), userId);
        }
        return mealOld != null && mealOld.getUser().getId() == userId ? crudRepository.save(meal) : null;
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
        /*return crudRepository.findAll().stream()
                .filter(meal -> meal.getUser().getId()==userId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
         */
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.getBetweenHalfOpen(startDateTime, endDateTime, userId);
        /*return getAll(userId).stream()
                .filter(meal -> Util.isBetweenHalfOpen(meal.getDateTime(),startDateTime,endDateTime))
                .collect(Collectors.toList());

         */
    }
}
