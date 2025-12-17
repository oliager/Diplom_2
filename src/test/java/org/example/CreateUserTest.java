package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.LoginUser;
import org.example.model.User;
import org.example.steps.UsersSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.http.HttpStatus;

import static org.hamcrest.CoreMatchers.is;

public class CreateUserTest extends BaseTest {
    private UsersSteps usersSteps = new UsersSteps();
    private String email;
    private String password;
    private String name;


    @Before
    public void setUp() {
        email = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "@mail.ru";
        name = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        password = RandomStringUtils.randomAlphabetic(6);
    }

    @Test
    @DisplayName("Создание нового уникального пользователя")
    @Description("Проверка, что при создании уникального пользователя" +
            "приходит ответ OK, в теле ответа есть email и name")
    public void createUniqueUser() {
        User user = new User(email, password, name);

        usersSteps.create(user)
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("user.email", is(email))
                .body("user.name", is(name));
    }

    @Test
    @DisplayName("Создание существующего пользователя")
    @Description("Проверка, что при создании ранее созданного пользователя" +
            "приходит ответ FORBIDDEN, в теле ответа ключи: success- false, message - User already exists")
    public void createExistingUser() {
        User user = new User(email, password, name);

        usersSteps.create(user).statusCode(HttpStatus.SC_OK);
        usersSteps.create(user)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без Email")
    @Description("Проверка, что при создании пользователя без Email" +
            "приходит ответ FORBIDDEN, в теле ответа ключи: success- false, " +
            "message - Email, password and name are required fields")
    public void createUserWithoutEmail() {
        User user = new User(null, password, name);

        usersSteps.create(user)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Создание пользователя без Пароля")
    @Description("Проверка, что при создании пользователя без Пароля" +
            "приходит ответ FORBIDDEN, в теле ответа ключи: success- false, " +
            "message - Email, password and name are required fields")
    public void createUserWithoutPassword() {
        User user = new User(email, null, name);

        usersSteps.create(user)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Создание пользователя без Имя")
    @Description("Проверка, что при создании пользователя без Имя" +
            "приходит ответ FORBIDDEN, в теле ответа ключи: success- false, " +
            "message - Email, password and name are required fields")
    public void createUserWithoutName() {
        User user = new User(email, password, null);

        usersSteps.create(user)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @After
    public void teardown() {
        LoginUser loginUser = new LoginUser(email, password);
        String accessToken = usersSteps.login(loginUser)
                .extract().path("accessToken");
        if (accessToken != null) {
            usersSteps.delete(accessToken);
        }
    }
}
