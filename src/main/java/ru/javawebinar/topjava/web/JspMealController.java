package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
public class JspMealController {

    Logger log = LoggerFactory.getLogger(JspMealController.class);
    @Autowired
    private MealService mealService;

    @GetMapping(value = "/meals")
    public String doGet(HttpServletRequest request, Model model) throws ServletException, IOException {
        log.info("******getAll*******");
        String action = request.getParameter("action");
        if (action != null && action.equals("delete")) {
            log.info("**************delete mel{}  user {}", getId(request), SecurityUtil.authUserId());
            mealService.delete(getId(request), SecurityUtil.authUserId());
        } else if (action != null && action.equals("update")) {
            log.info("**********update meal id={}", getId(request));
            model.addAttribute(getMealById(getId(request)));
            return "mealForm";
        } else if (action != null && action.equals("create")) {
            model.addAttribute("meal", new Meal());
            return "mealForm";
        } else if (action != null && action.equals("filter")) {
            LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
            LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
            LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
            LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
            List<MealTo> mealTos = MealsUtil.getTos(mealService.getBetweenInclusive(startDate, endDate, SecurityUtil.authUserId()), SecurityUtil.authUserId());
            model.addAttribute("meals", mealTos);
            return "meals";
        }
        List<MealTo> mealTos = MealsUtil.getTos(mealService.getAll(SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay());
        model.addAttribute("meals", mealTos);
        return "meals";
    }

    @PostMapping("/meals")
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        if (StringUtils.hasLength(request.getParameter("id"))) {
            meal.setId(getId(request));
            log.info("update meal {}", meal.toString());
            mealService.update(meal, SecurityUtil.authUserId());
        } else {
            log.info("create new meal {}", meal);
            mealService.create(meal, SecurityUtil.authUserId());
        }
        response.sendRedirect("meals");
    }

    @DeleteMapping("/meals")
    public void doDelete(){}

    public Meal getMealById(int id) {
        return mealService.get(id, SecurityUtil.authUserId());
    }

    public int getId(HttpServletRequest request) {
        String id = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(id);
    }

}
