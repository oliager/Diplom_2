package org.example;

import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.LoginUser;
import org.example.model.Order;
import org.example.model.User;
import org.example.steps.OrderSteps;
import org.example.steps.UsersSteps;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest extends BaseTest {

    private UsersSteps usersSteps = new UsersSteps();
    private OrderSteps orderSteps = new OrderSteps();
    private String authorization;

    @Before
    public void setUp() {
        String expectedEmail = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "@mail.ru";
        String expectedName = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        String password = RandomStringUtils.randomAlphabetic(6);

        User user = new User(expectedEmail, password, expectedName);
        LoginUser loginUser = new LoginUser(expectedEmail, password);

        usersSteps.create(user)
                .statusCode(200);
        authorization = usersSteps.login(loginUser)
                .statusCode(200)
                .extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа для авторизованного пользователя")
    public void createOrderForAuthorizedUser() {

        List<String> ingredientIds = orderSteps.getIngredients().extract().path("data._id");
        String ingredientId = ingredientIds.get(0);
        List<String> listIds = new ArrayList<>();
        listIds.add(ingredientId);
        Order order = new Order(listIds, authorization);

        orderSteps.createOrder(order)
                .statusCode(200)
                .body("order.number", notNullValue())
                .body("success", is(true));
    }
    @Test
    @DisplayName("Создание заказа для неавторизованного пользователя")
    public void createOrderForUnauthorizedUser() {

        List<String> ingredientIds = orderSteps.getIngredients().extract().path("data._id");
        String ingredientId = ingredientIds.get(0);
        List<String> listIds = new ArrayList<>();
        listIds.add(ingredientId);
        Order order = new Order(listIds, null);

        orderSteps.createOrder(order)
                .statusCode(200)
                .body("order.number", notNullValue())
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {

        Order order = new Order(null, authorization);
        orderSteps.createOrder(order)
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неправильным Хэшем ингредиента")
    public void createOrderWithBadIngredientHash() {

        List<String> listHashes = new ArrayList<>();
        listHashes.add(RandomStringUtils.randomAlphabetic(16).toLowerCase());
        Order order = new Order(listHashes, authorization);

        orderSteps.createOrder(order)
                .statusCode(500);
    }
}
