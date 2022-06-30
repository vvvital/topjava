package ru.javawebinar.topjava.repository.jpa;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user = entityManager.getReference(User.class, userId);
        if (meal.isNew()) {
            meal.setUser(user);
            entityManager.persist(meal);
            return meal;
        } else {
            Meal mealOld = get(meal.getId(), userId);
            if (mealOld != null) {
                meal.setUser(user);
                return entityManager.merge(meal);
            } else {
                return null;
            }
        }
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return entityManager.createQuery(Meal.DELETE_ID).setParameter(1, id).setParameter(2, userId).executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        TypedQuery<Meal> query = entityManager.createQuery(Meal.GET_ID, Meal.class);
        query.setParameter(1, userId);
        query.setParameter(2, id);
        return DataAccessUtils.singleResult(query.getResultList());
    }

    @Override
    public List<Meal> getAll(int userId) {
        return entityManager.createQuery(Meal.GET_ALL, Meal.class).setParameter(1, userId).getResultList().stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        TypedQuery<Meal> query = entityManager.createQuery(Meal.GET_BETWEEN, Meal.class);
        query.setParameter(1, userId);
        query.setParameter(2, startDateTime);
        query.setParameter(3, endDateTime);
        return query.getResultList().stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}