package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.LoginUser;
import org.example.model.Order;
import org.example.model.User;
import org.example.steps.OrderSteps;
import org.example.steps.UsersSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

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
                .statusCode(HttpStatus.SC_OK);
        authorization = usersSteps.login(loginUser)
                .statusCode(HttpStatus.SC_OK)
                .extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа для авторизованного пользователя")
    @Description("Проверка, что при создании заказа для авторизованного пользователя " +
            "приходит код ответа 200 ОК, тело содержит номер заказа, а ключ success - true")
    public void createOrderForAuthorizedUser() {
        List<String> ingredientIds = orderSteps.getIngredients().extract().path("data._id");
        String ingredientId = ingredientIds.get(0);
        List<String> listIds = new ArrayList<>();
        listIds.add(ingredientId);
        Order order = new Order(listIds, authorization);

        orderSteps.createOrder(order)
                .statusCode(HttpStatus.SC_OK)
                .body("order.number", notNullValue())
                .body("success", is(true));
    }
    @Test
    @DisplayName("Создание заказа для неавторизованного пользователя")
    @Description("Проверка, что при создании заказа для неавторизованного пользователя " +
            "приходит код ответа 200 ОК, тело содержит номер заказа, а ключ success - true")
    public void createOrderForUnauthorizedUser() {
        List<String> ingredientIds = orderSteps.getIngredients().extract().path("data._id");
        String ingredientId = ingredientIds.get(0);
        List<String> listIds = new ArrayList<>();
        listIds.add(ingredientId);
        Order order = new Order(listIds, null);

        orderSteps.createOrder(order)
                .statusCode(HttpStatus.SC_OK)
                .body("order.number", notNullValue())
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка, что при создании заказа без списка хэшей ингредиентов " +
            "приходит ответ BAD_REQUEST, тело содержит ключ success - false")
    public void createOrderWithoutIngredients() {
        Order order = new Order(null, authorization);
        orderSteps.createOrder(order)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неправильным Хэшем ингредиента")
    @Description("Проверка, что при создании заказа со списком ингредиентов, содержащим " +
            "несуществующий хэш приходит ответ INTERNAL_SERVER_ERROR")
    public void createOrderWithBadIngredientHash() {
        List<String> listHashes = new ArrayList<>();
        listHashes.add(RandomStringUtils.randomAlphabetic(16).toLowerCase());
        Order order = new Order(listHashes, authorization);

        orderSteps.createOrder(order)
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void teardown() {
        if (authorization!=null) {
            usersSteps.delete(authorization);
        }
    }
}
