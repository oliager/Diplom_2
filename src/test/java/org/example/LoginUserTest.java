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
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest extends BaseTest {

    private UsersSteps usersSteps = new UsersSteps();
    private String email;
    private String password;
    private String name;
    private LoginUser loginUser;


    @Before
    public void setUp(){
        email = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "@mail.ru";
        password = RandomStringUtils.randomAlphabetic(6);
        name = RandomStringUtils.randomAlphabetic(6).toLowerCase();

        User user = new User(email, password, name);

        usersSteps.create(user)
                .statusCode(HttpStatus.SC_OK);
    }
    @Test
    @DisplayName("Авторизация для существующего пользователя")
    @Description("Проверка, что при авторизации для существующего пользователя" +
            "приходит ответ OK, в теле ответа есть значения accessToken,refreshToken," +
            "а также email, name")
    public void loginForExistingUser() {
        loginUser = new LoginUser(email, password);

        usersSteps.login(loginUser)
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", is(email))
                .body("user.name", is(name));
    }
    @Test
    @DisplayName("Авторизация с неправильным Email")
    @Description("Проверка, что при авторизации пользователя без Email" +
            "приходит ответ UNAUTHORIZED, в теле ответа ключи: success- false, " +
            "message - email or password are incorrect")
    public void loginUserWithWrongEmail() {
        loginUser = new LoginUser(null, password);

        usersSteps.login(loginUser)
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }
    @Test
    @DisplayName("Авторизация с неправильным паролем")
    @Description("Проверка, что при авторизации пользователя без пароля" +
            "приходит ответ UNAUTHORIZED, в теле ответа ключи: success- false, " +
            "message - email or password are incorrect")
    public void loginUserWithWrongPassword() {
        loginUser = new LoginUser(email, null);

        usersSteps.login(loginUser)
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @After
    public void teardown() {
        String accessToken = usersSteps.login(loginUser)
                .extract().path("accessToken");
        if (accessToken!=null) {
            usersSteps.delete(accessToken);
        }
    }
}
