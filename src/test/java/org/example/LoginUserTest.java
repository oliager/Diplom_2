package org.example;

import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.LoginUser;
import org.example.model.User;
import org.example.steps.UsersSteps;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest extends BaseTest {

    private UsersSteps usersSteps = new UsersSteps();
    private String expectedEmail;
    private String password;
    private String expectedName;
    User user;

    @Before
    public void setUp(){
        expectedEmail = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "@mail.ru";
        password = RandomStringUtils.randomAlphabetic(6);
        expectedName = RandomStringUtils.randomAlphabetic(6).toLowerCase();

        user = new User(expectedEmail, password, expectedName);

        usersSteps.create(user)
                .statusCode(200);
    }
    @Test
    @DisplayName("Авторизация для существующего пользователя")
    public void loginForExistingUser() {
        LoginUser loginUser = new LoginUser(expectedEmail, password);

        usersSteps.login(loginUser)
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", is(expectedEmail))
                .body("user.name", is(expectedName));
    }
    @Test
    @DisplayName("Авторизация с неправильным Email")
    public void loginUserWithWrongEmail() {
        LoginUser loginUser = new LoginUser(null, password);

        usersSteps.login(loginUser)
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }
    @Test
    @DisplayName("Авторизация с неправильным паролем")
    public void loginUserWithWrongPassword() {
        LoginUser loginUser = new LoginUser(expectedEmail, null);

        usersSteps.login(loginUser)
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }
}
