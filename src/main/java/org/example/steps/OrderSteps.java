package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.model.LoginUser;
import org.example.model.Order;
import org.example.model.User;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .body(order)
                .when()
                .post("api/orders")
                .then();

    }
    @Step("Получение списка ингредиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .get("api/ingredients")
                .then();

    }


}
