package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.model.LoginUser;
import org.example.model.User;

import static io.restassured.RestAssured.given;
import static org.example.config.RestConfig.*;

public class UsersSteps {

    @Step("Создание пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .body(user)
                .when()
                .post(POST_REGISTER)
                .then();

    }

    @Step("Авторизация пользователя")
    public ValidatableResponse login(LoginUser loginUser) {
        return given()
                .body(loginUser)
                .when()
                .post(POST_LOGIN)
                .then();

    }
    @Step("Удаляется аккаунт пользователя")
    public ValidatableResponse delete(String authorization) {
        return given()
                .header("Authorization", authorization)
                .when()
                .delete(DELETE_USER)
                .then();
    }
}
