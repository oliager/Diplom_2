package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.model.Order;

import static io.restassured.RestAssured.given;
import static org.example.config.RestConfig.*;

public class OrderSteps {

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .body(order)
                .when()
                .post(POST_ORDERS)
                .then();

    }
    @Step("Получение списка ингредиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .get(GET_INGREDIENTS)
                .then();

    }


}
