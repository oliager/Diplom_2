package org.example;

import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.User;
import org.example.steps.UsersSteps;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class CreateUserTest extends BaseTest {
    private UsersSteps usersSteps = new UsersSteps();

    @Test
    @DisplayName("Создание нового уникального пользователя")
    public void createUniqueUser() {
        String expectedEmail = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "@mail.ru";
        String expectedName = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        String password = RandomStringUtils.randomAlphabetic(6);

        User user = new User(expectedEmail, password, expectedName);

        usersSteps.create(user)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(expectedEmail))
                .body("user.name", is(expectedName));
    }

    @Test
    @DisplayName("Создание существующего пользователя")
    public void createExistingUser() {
        String email = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "@mail.ru";
        String name = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        String password = RandomStringUtils.randomAlphabetic(6);

        User user = new User(email, password, name);

        usersSteps.create(user).statusCode(200);
        usersSteps.create(user)
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без Email")
    public void createUserWithoutEmail() {
        String name = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        String password = RandomStringUtils.randomAlphabetic(6);

        User user = new User(null, password, name);

        usersSteps.create(user)
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }
}
