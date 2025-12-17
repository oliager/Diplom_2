package org.example.config;

public class RestConfig {
    public static final String HOST = "https://stellarburgers.education-services.ru/";

   //эндпойнты для юзера
    public static final String POST_REGISTER = "api/auth/register";
    public static final String POST_LOGIN = "api/auth/login";
    public static final String DELETE_USER = "api/auth/user";


    //эндпойнты для заказа
    public static final String POST_ORDERS = "api/orders";
    public static final String GET_INGREDIENTS = "api/ingredients";

}