package org.example.model;

import java.util.List;

public class Order {
    private final List<String> ingredients;
    private final String authorization;
    public Order(List<String> ingredients, String authorization) {
        this.ingredients = ingredients;
        this.authorization=authorization;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
    public String getAuthorization() {
        return authorization;
    }
}
