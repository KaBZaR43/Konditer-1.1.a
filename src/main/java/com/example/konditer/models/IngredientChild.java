package com.example.konditer.models;

public class IngredientChild {
    private final String childName;

    public IngredientChild(String childName) {
        this.childName = childName;
    }

    public String getChildName() {
        return childName;
    }

    @Override
    public String toString() {
        return childName;
    }
}